package com.brandedCompany.controller.customer;

import com.brandedCompany.domain.Order;
import com.brandedCompany.domain.OrderItem;
import com.brandedCompany.domain.Product;
import com.brandedCompany.domain.handler.ProductPageHandler;
import com.brandedCompany.serivce.OptionService;
import com.brandedCompany.util.CustomerControllerUtils;
import com.brandedCompany.util.DomainUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.*;

import static com.brandedCompany.util.DomainUtils.TABLE.*;
import static com.brandedCompany.util.DomainUtils.parseBigInt;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@Controller
@RequestMapping("/customer/{customerId}/order")
public class CustomerOrderController
{
    final Logger logger = LoggerFactory.getLogger(CustomerOrderController.class);

    private final OptionService service;
    private final CustomerControllerUtils util;

    @Autowired
    public CustomerOrderController(OptionService service,
                                   CustomerControllerUtils util)
    {
        this.service = service;
        this.util = util;
    }


    // 주문상태 변경
    @Transactional(rollbackFor = Exception.class)
    @PatchMapping("/{orderId}")
    public ResponseEntity<BigInteger> returnOrder(
        @PathVariable("customerId")
            BigInteger customerId,
        @PathVariable("orderId")
            BigInteger orderId, String status) throws Exception
    {
        Order originalOrder = (Order) service.select(ORDERS, orderId);
        Order orderBeReturned = new Order(originalOrder.getOrderId(), originalOrder.getCustomerId(), BigInteger.ONE, status, originalOrder.getOrderDate());

        if (!service.update(orderBeReturned))
        {
            logger.error("[" + customerId + "] 주문취소 실패");
            return new ResponseEntity<>(customerId,BAD_REQUEST);
        }
        logger.info("[" + customerId + "] 주문취소");
        return new ResponseEntity<>(customerId,OK);
    }

    @Transactional(rollbackFor = Exception.class)
    @PostMapping
    public ResponseEntity<BigInteger> insert(
        @PathVariable
            BigInteger customerId,
        @RequestBody
            ObjectNode objectNode) throws Exception
    {
        // 1. 'ORDER' 테이블 데이터 추가 -> 'ORDER_ITEMS' 테이블 데이터 추가
        ObjectMapper objectMapper = new ObjectMapper();

        ResponseEntity<BigInteger> response = new ResponseEntity<>(customerId, OK);

        // 새로운 주문 번호 세팅-> ORDER_ID 시퀀스 값 1 증가
        BigInteger nextOrderId = service.selectNextSequence(ORDERS);
        // 주문 정보
        List<Object> rows = (List<Object>) objectMapper.treeToValue(objectNode.get("orders"), Object.class);
        //        System.out.println("rows = " + rows);
        for (Object row : rows)
        {
            // 주문 정보 map 형태로 변환 -> 신규 주문 번호 추가
            ((Map<String, Object>) row).put("orderId", nextOrderId);
            // 'Order' 객체로 변환
            Order order = (Order) DomainUtils.packDomain(row);
            //            System.out.println("order in con= " + order);
            // ORDER 테이블 데이터 추가 -> 'ORDER' 테이블에 'Order' 정보 데이터베이스에 추가
            if (!service.insert(order))
            {
                response = new ResponseEntity<>(customerId, HttpStatus.INTERNAL_SERVER_ERROR);
                logger.error("[" + customerId + "] " + order.getOrderId() + "번 주문실패");
            }
            logger.info("[" + customerId + "] " + order.getOrderId() + "번 주문성공");
        }

        rows = (List<Object>) objectMapper.treeToValue(objectNode.get("orderItems"), Object.class);
        int itemId = 0;
        for (Object row : rows)
        {
            // 주문 정보 map 형태로 변환 -> 신규 주문 번호 추가
            ((Map<String, Object>) row).put("orderId", nextOrderId);
            // 주문아이템 정보 map 형태로 변환 -> 신규 주문 번호 추가
            //            ((Map<String, Object>) row).put("itemId", service.selectNextSequence(ORDER_ITEMS));
            ((Map<String, Object>) row).put("itemId", (itemId += 1));

            // 'OrderItems' 객체로 변환
            OrderItem orderItem = (OrderItem) DomainUtils.packDomain(row);
            // ORDER_ITEMS 테이블 데이터 추가 -> 'ORDER_ITEMS' 테이블에 'OrderItem' 정보 데이터베이스에 추가
            if (!service.insert(orderItem))
            {

                response = new ResponseEntity<>(customerId, HttpStatus.INTERNAL_SERVER_ERROR);
                logger.error("[" + customerId + "] " + orderItem.getOrderId() + "번 주문의 " + orderItem.getItemId() + "번 상품 추가실패");
            }
            logger.info("[" + customerId + "] " + orderItem.getOrderId() + "번 주문의 " + orderItem.getItemId() + "번 상품 추가성공");
        }

        // 2. 카트 비우기
        // 장바구니에 담은 데이터가 있으면 카트 비우기
        rows = objectMapper.treeToValue(objectNode.get("carts"), ArrayList.class);

        if (Optional.ofNullable(rows)
                    .isPresent())
            for (Object row : rows)
            {
                customerId = parseBigInt((Map<String, Object>) row, "customerId");
                BigInteger productId = parseBigInt((Map<String, Object>) row, "productId");

                // 카트 정보 삭제
                if (!service.delete(CARTS, customerId, productId))
                {
                    response = new ResponseEntity<BigInteger>(customerId, HttpStatus.INTERNAL_SERVER_ERROR);
                    logger.error("[" + customerId + "] " + productId + "번 상품 카트에서 비우기 에러");
                }
                else
                    logger.info("[" + customerId + "] " + productId + "번 상품 카트에서 비우기 성공");
            }
        return response;
    }

    private List<Product> addProduct(Map<String, Object> prdMap) throws Exception
    {
        List<Product> product = new ArrayList<>();

        prdMap.forEach((s, o) ->
                       {
                           List<Object> list = (ArrayList<Object>) o;
                           for (Object unpackProduct : list)
                           {
                               Map map = (Map) unpackProduct;

                               BigInteger productId = BigInteger.valueOf(Long.parseLong(String.valueOf(map.get("productId"))));
                               BigInteger categoryId = BigInteger.valueOf(Long.parseLong(String.valueOf(map.get("categoryId"))));
                               String productName = String.valueOf(map.get("productName"));
                               String description = String.valueOf(map.get("description"));
                               Double standardCost = Double.valueOf(String.valueOf(map.get("standardCost")));
                               Double listPrice = Double.valueOf(String.valueOf(map.get("listPrice")));
                               Product prd = new Product(productId, categoryId, productName, description, standardCost, listPrice);
                               product.add(prd);
                           }

                       });
        return product;
    }


}

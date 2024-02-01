package com.brandedCompany.controller.customer;

import com.brandedCompany.domain.Cart;
import com.brandedCompany.domain.handler.CartPageHandler;
import com.brandedCompany.serivce.PagingAndSortingService;
import com.brandedCompany.util.CustomerControllerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.List;

import static com.brandedCompany.util.DomainUtils.TABLE.CARTS;

@Controller
@RequestMapping("/customer/{customerId}/cart")
public class CustomerCartController
{
    final Logger logger = LoggerFactory.getLogger(CustomerCartController.class);
    final CustomerControllerUtils utils;
    private final PagingAndSortingService service;

    @Autowired
    public CustomerCartController(
        @Qualifier("pagingAndSortingServiceImpl")
            PagingAndSortingService service, CustomerControllerUtils utils)
    {
        this.service = service;
        this.utils = utils;
    }

    @PatchMapping
    public ResponseEntity<BigInteger> adjustItemQuantity(
        @PathVariable
            BigInteger customerId,
        @RequestBody
            Cart cart, HttpServletRequest request, Model model)
    {

        ResponseEntity<BigInteger> response = new ResponseEntity<>(customerId, HttpStatus.OK);
        try
        {
            if (!service.update(cart))
                response = new ResponseEntity<>(customerId, HttpStatus.BAD_REQUEST);
            CartPageHandler pageHandler = utils.getCartPageHandler(customerId, request);
            model.addAttribute("pageHandler", pageHandler);
        }
        catch (Exception e)
        {
            //       e.printStackTrace();
            return new ResponseEntity<>(customerId, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @GetMapping
    public String goToPage(
        @PathVariable
            BigInteger customerId, HttpServletRequest request, Model model) throws Exception
    {
//        model.addAttribute("customerId", customerId);

        // 장바구니 품목 페이징 핸들러
        CartPageHandler pageHandler = utils.getCartPageHandler(customerId, request);
        model.addAttribute("pageHandler", pageHandler);
        // 장바구니 품목
        model.addAttribute("items", service.pageAndSort(pageHandler.getSearchCondition()));
        logger.info("[" + customerId + "] 장바구니 페이지");
        return "customerCart";
    }

    @DeleteMapping
    public ResponseEntity<BigInteger> delete(
        @PathVariable
            BigInteger customerId,
        @RequestBody
            List<BigInteger> productIds) throws Exception
    {
        boolean isDeletedAll = productIds.stream()
                                         .allMatch(id ->
                                                   {
                                                       try
                                                       {
                                                           logger.info("[" + customerId + "]" + id + "번 상품 삭제성공");
                                                           return service.delete(CARTS, customerId, id);
                                                       }
                                                       catch (Exception e)
                                                       {
                                                           logger.error("[" + customerId + "]" + id + "번 상품 삭제실패");
                                                           //                e.printStackTrace();
                                                       }
                                                       return false;
                                                   });
        if (!isDeletedAll)
            return new ResponseEntity<>(customerId, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(customerId, HttpStatus.OK);
    }


    @Transactional(rollbackFor = Exception.class)
    @PostMapping
    public ResponseEntity<BigInteger> insert(
        @PathVariable
            BigInteger customerId,
        @RequestBody
            List<Cart> carts) throws Exception
    {
        ResponseEntity<BigInteger> response = new ResponseEntity<>(customerId, HttpStatus.BAD_REQUEST);

        for (Cart cart : carts)
        {
            if (service.isExist(CARTS, cart.getCustomerId(), cart.getProductId()))
            {
                Cart baseCart = (Cart) service.select(CARTS, cart.getCustomerId(), cart.getProductId());

                if (cart.getProductId()
                        .equals(baseCart.getProductId()))
                {
                    Double cumulativeUnitPrice = cart.getUnitPrice() + baseCart.getUnitPrice();
                    BigInteger cumulativeQuantity = cart.getQuantity()
                                                        .add(baseCart.getQuantity());

                    Cart cumulativeCart = new Cart(baseCart.getCustomerId(), baseCart.getProductId(), cumulativeQuantity, baseCart.getProductName(), cumulativeUnitPrice, baseCart.getIncludedDate());

                    // 장바구니 갱신
                    if (service.update(cumulativeCart))
                    {
                        logger.info("[" + customerId + "] 장바구니 업데이트 성공");
                        response = new ResponseEntity<>(customerId, HttpStatus.OK);
                    }
                    else
                    {
                        logger.error("[" + customerId + "] 장바구니 업데이트 실패");
                        return response;
                    }
                }
            }
            // 장바구니에 찜 상품 추가
            else if (service.insert(cart))
            {
                response = new ResponseEntity<>(customerId, HttpStatus.OK);
                logger.info("[" + customerId + "] 장바구니에 새 상품추가 성공");
            }
            else
            {
                logger.error("[" + customerId + "] 장바구니에 새 상품추가 실패");
                return response;
            }
        }
        return response;
    }


}


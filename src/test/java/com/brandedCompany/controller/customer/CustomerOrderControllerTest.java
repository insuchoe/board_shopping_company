package com.brandedCompany.controller.customer;

import com.brandedCompany.authentication.CustomerAuthentication;
import com.brandedCompany.authentication.CustomerAuthenticationManager;
import com.brandedCompany.authentication.exception.AuthenticationRuntimeException;
import com.brandedCompany.domain.Customer;
import com.brandedCompany.domain.Order;
import com.brandedCompany.domain.OrderItem;
import com.brandedCompany.serivce.OptionServiceImpl;
import com.brandedCompany.util.DomainUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.brandedCompany.util.DomainUtils.TABLE.ORDERS;
import static com.brandedCompany.util.DomainUtils.TABLE.ORDER_ITEMS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/root-context.xml"})
@Sql({"classpath:schema/brandedCompany_truncate.sql", "classpath:schema/brandedCompany_data.sql"})
public class CustomerOrderControllerTest
{
    private MockMvc mvc;
    @Autowired CustomerOrderController controller;
    @Autowired CustomerProductController productController;
    @Autowired CustomerAuthenticationManager manager;
    @Autowired OptionServiceImpl service;
    final BigInteger CUSTOMER_ID = BigInteger.valueOf(49);

    @Before
    public void setUp()
    {
        InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();

        internalResourceViewResolver.setPrefix("/WEB-INF/views/");
        internalResourceViewResolver.setSuffix(".jsp");
        mvc = MockMvcBuilders.standaloneSetup(controller)
                             .setViewResolvers(internalResourceViewResolver)
                             .build();

        try
        {
            final Customer CUSTOMER = (Customer) service.select(DomainUtils.TABLE.CUSTOMERS, CUSTOMER_ID);
            CustomerAuthentication authentication = new CustomerAuthentication(CUSTOMER);
            if (!manager.hasAuthentication(authentication))
                manager.addAuthentication(authentication);
        }
        catch (Exception | AuthenticationRuntimeException e)
        {
            //    e.printStackTrace();
        }
    }

    public void setUp(Object controller)
    {

        InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();

        internalResourceViewResolver.setPrefix("/WEB-INF/views/");
        internalResourceViewResolver.setSuffix(".jsp");
        mvc = MockMvcBuilders.standaloneSetup(controller)
                             .setViewResolvers(internalResourceViewResolver)
                             .build();

        try
        {
            final Customer CUSTOMER = (Customer) service.select(DomainUtils.TABLE.CUSTOMERS, CUSTOMER_ID);
            CustomerAuthentication authentication = new CustomerAuthentication(CUSTOMER);
            if (!manager.hasAuthentication(authentication))
                manager.addAuthentication(authentication);
        }
        catch (Exception | AuthenticationRuntimeException e)
        {
            //      e.printStackTrace();
        }
    }

    @Test
    public void goOrderPage()
    {
        try
        {
            setUp(productController);
            mvc.perform(get("/customer/" + CUSTOMER_ID + "/product"))
                .andDo(print())
               .andExpect(view().name("customerProduct"))
               .andExpect(status().isOk());
        }
        catch (Exception e)
        {
            //   e.printStackTrace();
        }
    }

    @DisplayName("49번 고객은 228번 상품 1개를 주문한다.")
    @Test
    public void customer49Orders1Product228()
    {
        //given
        // ORDERS
        List<Map<String, Object>> orders = new ArrayList<>();
        HashMap<String, Object> orderElements = new HashMap<>();
        orderElements.put("customerId", CUSTOMER_ID);
        orderElements.put("status", Order.OrderStatus.Pending);
        orders.add(orderElements);

        // ORDER_ITEMS
        List<Map<String, Object>> orderItems = new ArrayList<>();
        HashMap<String, Object> orderItemsElements = new HashMap<>();
        orderItemsElements.put("productId", BigInteger.valueOf(228));
        orderItemsElements.put("quantity", 1);
        orderItemsElements.put("unitPrice", 3410.46);
        orderItems.add(orderItemsElements);

        Map<String, List<Map<String, Object>>> objectMap = new HashMap<>();
        objectMap.put("orders", orders);
        objectMap.put("orderItems", orderItems);
        try
        {
            //ORDER,ORDER_ITEMS 직렬화
            String objectMapToJson = new ObjectMapper().writeValueAsString(objectMap);
            //when
            mvc.perform(post("/customer/" + CUSTOMER_ID + "/order").content(objectMapToJson)
                                                                   .contentType(MediaType.APPLICATION_JSON))
               //then
               .andExpect(status().isOk());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }


    @DisplayName("49번 고객은 228번 상품을 3개 주문한다.")
    @Test
    public void customer49Orders3Items228()
    {
        // given
        // ORDER
        List<Map<String, Object>> orders = new ArrayList<>();
        HashMap<String, Object> orderElements = new HashMap<>();
        // ORDER_ITEMS
        List<Map<String, Object>> orderItems = new ArrayList<>();
        HashMap<String, Object> orderItemsElements = new HashMap<>();

        orderElements.put("customerId", CUSTOMER_ID);
        orderElements.put("status", Order.OrderStatus.Pending);
        orders.add(orderElements);

        orderItemsElements.put("productId", BigInteger.valueOf(228));
        orderItemsElements.put("quantity", 3);
        orderItemsElements.put("unitPrice", 3410.46);
        orderItems.add(orderItemsElements);

        Map<String, List<Map<String, Object>>> objectMap = new HashMap<>();

        objectMap.put("orders", orders);
        objectMap.put("orderItems", orderItems);

        try
        {
            String objectMapToJson = new ObjectMapper().writeValueAsString(objectMap);
            // when
            // 228번 상품 3개 주문요청
            mvc.perform(post("/customer/" + CUSTOMER_ID + "/order").content(objectMapToJson)
                                                                   .contentType(MediaType.APPLICATION_JSON))
               // then
               .andExpect(status().isOk());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @DisplayName("49번 고객은 228번 상품 3개와 248번 상품 3개를 주문한다.")
    @Test
    public void customer49Orders3Products228And3Products248()
    {
        //given
        // ORDER
        List<Map<String, Object>> orders = new ArrayList<>();
        HashMap<String, Object> orderElements = new HashMap<>();
        // ORDER_ITEMS
        List<Map<String, Object>> orderItems = new ArrayList<>();
        HashMap<String, Object> orderItemsElements = new HashMap<>();

        orderElements.put("customerId", CUSTOMER_ID);
        orderElements.put("status", Order.OrderStatus.Pending);
        orders.add(orderElements);


        orderItemsElements.put("productId", BigInteger.valueOf(228));
        orderItemsElements.put("quantity", 3);
        orderItemsElements.put("unitPrice", 3410.46);
        orderItems.add(orderItemsElements);

        HashMap<String, Object> orderItemsElements2 = new HashMap<>();
        orderItemsElements2.put("productId", BigInteger.valueOf(248));
        orderItemsElements2.put("quantity", 3);
        orderItemsElements2.put("unitPrice", 2774.98);
        orderItems.add(orderItemsElements2);

        Map<String, List<Map<String, Object>>> objectMap = new HashMap<>();

        objectMap.put("orders", orders);
        objectMap.put("orderItems", orderItems);

        try
        {
            String objectMapToJson = new ObjectMapper().writeValueAsString(objectMap);
            //when
            // 228번 상품 3개, 248번 상품 3개 주문
            mvc.perform(post("/customer/" + CUSTOMER_ID + "/order").content(objectMapToJson)
                                                                   .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @DisplayName("49번 고객이 전체반품을 요청한다.")
    @Test
    public void customer49RequestsAFullReturn()
    {
        try
        {
            // given
            // 주문자 번호
            final BigInteger ORDERER_ID = BigInteger.valueOf(49);
            // 주문 번호
            final BigInteger ORDER_ID = service.selectNextSequence(ORDERS);

            // 상품번호
            final BigInteger ONE_PRODUCT_ID = BigInteger.valueOf(228);
            final BigInteger ANOTHER_PRODUCT_ID = BigInteger.valueOf(248);
            final BigInteger THE_OTHER_PRODUCT_ID = BigInteger.valueOf(268);
            // 주문상품 상품의 아이템 번호
            final BigInteger ONE_ITEM_ID=BigInteger.ONE;
            final BigInteger ANOTHER_ITEM_ID=BigInteger.TWO;
            final BigInteger THE_OTHER_ITEM_ID=BigInteger.valueOf(3);

            // 주문, 주문 아이템
            service.insert(new Order(ORDER_ID, ORDERER_ID, Order.OrderStatus.Pending));
            service.insert(new OrderItem(ORDER_ID, ONE_ITEM_ID, ONE_PRODUCT_ID,100.0,3410.46));
            service.insert(new OrderItem(ORDER_ID, ANOTHER_ITEM_ID, ANOTHER_PRODUCT_ID,200.0,2774.98));
            service.insert(new OrderItem(ORDER_ID, THE_OTHER_ITEM_ID, THE_OTHER_PRODUCT_ID,150.0,47.88));

            // when
            // 전체 반품요청
            mvc.perform(patch("/customer/" + ORDERER_ID + "/order/"+ORDER_ID+"?status=Pending"))
               // then
               .andExpect(status().isOk());

            Order order = (Order) service.select(ORDERS, ORDER_ID, ORDERER_ID);
            Assertions.assertEquals(Order.OrderStatus.Pending,order.getStatus());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }



}
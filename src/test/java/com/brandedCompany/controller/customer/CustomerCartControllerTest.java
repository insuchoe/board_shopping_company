package com.brandedCompany.controller.customer;

import com.brandedCompany.domain.Cart;
import com.brandedCompany.domain.Product;
import com.brandedCompany.serivce.PagingAndSortingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.brandedCompany.util.DomainUtils.TABLE.CARTS;
import static com.brandedCompany.util.DomainUtils.TABLE.PRODUCTS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/root-context.xml"})
@Sql({"classpath:schema/brandedCompany_truncate.sql", "classpath:schema/brandedCompany_data.sql"})
public class CustomerCartControllerTest
{
    private MockMvc mvc;
    @Autowired CustomerCartController controller;
    @Qualifier("pagingAndSortingServiceImpl") @Autowired PagingAndSortingService service;

    @Before
    public void setUp()
    {
        InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
        internalResourceViewResolver.setPrefix("/WEB-INF/views/");
        internalResourceViewResolver.setSuffix(".jsp");
        mvc = MockMvcBuilders.standaloneSetup(controller)
                             .setViewResolvers(internalResourceViewResolver)
                             .build();
    }

    @DisplayName("49번 고객은 228번 상품 1개를 장바구니에 담는다.")
    @Test
    public void customer49Adds1Item228ToTheCart()
    {
        final BigInteger ORDERER_ID = BigInteger.valueOf(49);
        final BigInteger ITEM_ID = BigInteger.valueOf(228);
        final BigInteger ITEM_QUANTITY = BigInteger.ONE;
        try
        {
            List<Cart> carts = new ArrayList<>();
            Product product = (Product) service.select(PRODUCTS, ITEM_ID);
            // given
            carts.add(new Cart(ORDERER_ID, product.getProductId(), ITEM_QUANTITY, product.getProductName(), product.getListPrice(), LocalDateTime.now()));
            String serial = new ObjectMapper().registerModule(new JavaTimeModule())
                                              .writeValueAsString(carts);
            //when
            mvc.perform(post("/customer/" + ORDERER_ID + "/cart").contentType(MediaType.APPLICATION_JSON)
                                                                 .content(serial))
               //then
               .andExpect(status().isOk());

        }
        catch (Exception e)
        {
            //     e.printStackTrace();
        }


    }

    @DisplayName("49번 고객은 228번 상품 3개를 장바구니에 담는다.")
    @Test
    public void customer49Adds3Item228ToTheCart()
    {
        final BigInteger ORDERER_ID = BigInteger.valueOf(49);
        final BigInteger ITEM_ID = BigInteger.valueOf(228);
        final BigInteger ITEM_QUANTITY = BigInteger.valueOf(3);
        try
        {
            List<Cart> carts = new ArrayList<>();
            Product product = (Product) service.select(PRODUCTS, ITEM_ID);

            // given
            carts.add(new Cart(ORDERER_ID, product.getProductId(), ITEM_QUANTITY, product.getProductName(), product.getListPrice(), LocalDateTime.now()));
            String serial = new ObjectMapper().registerModule(new JavaTimeModule())
                                              .writeValueAsString(carts);

            mvc.perform(post("/customer/" + ORDERER_ID + "/cart").contentType(MediaType.APPLICATION_JSON)
                                                                 .content(serial))
               .andExpect(status().isOk());

        }
        catch (Exception e)
        {
            //        e.printStackTrace();
        }

    }

    @DisplayName("49번 고객은 228번 상품를 장바구니에서 삭제한다.")
    @Test
    public void customer49removes3Item228ToTheCart()
    {
        final BigInteger ORDERER_ID = BigInteger.valueOf(49);
        final BigInteger ITEM_ID = BigInteger.valueOf(228);
        final BigInteger ITEM_QUANTITY = BigInteger.valueOf(3);

        List<BigInteger> ITEM_IDS = new ArrayList<>();
        try
        {
            Product product = (Product) service.select(PRODUCTS, ITEM_ID);
            Cart cart = new Cart(ORDERER_ID, ITEM_ID, ITEM_QUANTITY, product.getProductName(), product.getListPrice(), LocalDateTime.now());

            //given
            service.insert(cart);
            ITEM_IDS.add(ITEM_ID);
            String serial = new ObjectMapper().writeValueAsString(ITEM_IDS);

            //when
            mvc.perform(delete("/customer/" + ORDERER_ID + "/cart").content(serial)
                                                                   .contentType(MediaType.APPLICATION_JSON))
               //then
               //상태코드-200
               //삭제상품 존재여부
               .andExpect(status().isOk());
            Assertions.assertFalse(service.isExist(CARTS, ORDERER_ID, ITEM_ID));

        }
        catch (Exception e)
        {
            //       e.printStackTrace();
        }

    }

    @DisplayName("49번 고객은 228,248,268번 상품 중 228번만 장바구니에서 삭제한다.")
    @Test
    public void customer49DeletesOnly3Items228OutOfItems228And248And268fromTheCart()
    {
        final BigInteger ORDERER_ID = BigInteger.valueOf(49);
        final BigInteger ONE_ITEM_ID = BigInteger.valueOf(228);
        final BigInteger ANOTHER_ITEM_ID = BigInteger.valueOf(248);
        final BigInteger THE_OTHER_ITEM_ID = BigInteger.valueOf(268);
        final BigInteger ONE_ITEM_QUANTITY = BigInteger.valueOf(6);
        final BigInteger ANOTHER_ITEM_QUANTITY = BigInteger.valueOf(10);
        final BigInteger THE_OTHER_ITEM_QUANTITY = BigInteger.valueOf(10);

        List<BigInteger> ITEM_IDS = new ArrayList<>();
        ITEM_IDS.add(ONE_ITEM_ID);
        try
        {
            Product oneProduct = (Product) service.select(PRODUCTS, ONE_ITEM_ID);
            Cart oneCart = new Cart(ORDERER_ID, ONE_ITEM_ID, ONE_ITEM_QUANTITY, oneProduct.getProductName(), oneProduct.getListPrice(), LocalDateTime.now());

            Product anotherProduct = (Product) service.select(PRODUCTS, ANOTHER_ITEM_ID);
            Cart anotherCart = new Cart(ORDERER_ID, ANOTHER_ITEM_ID, ANOTHER_ITEM_QUANTITY, anotherProduct.getProductName(), anotherProduct.getListPrice(), LocalDateTime.now());

            Product theOtherProduct = (Product) service.select(PRODUCTS, THE_OTHER_ITEM_ID);
            Cart theOtherCart = new Cart(ORDERER_ID, THE_OTHER_ITEM_ID, THE_OTHER_ITEM_QUANTITY, theOtherProduct.getProductName(), theOtherProduct.getListPrice(), LocalDateTime.now());

            //given
            service.insert(oneCart);
            service.insert(anotherCart);
            service.insert(theOtherCart);

            String serial = new ObjectMapper().writeValueAsString(ITEM_IDS);

            //when
            mvc.perform(delete("/customer/" + ORDERER_ID + "/cart").content(serial)
                                                                   .contentType(MediaType.APPLICATION_JSON))
               //then
               //상태코드-200
               //삭제상품 존재여부
               .andExpect(status().isOk());
            Assertions.assertFalse(service.isExist(CARTS, ORDERER_ID, ONE_ITEM_ID));
            Assertions.assertTrue(service.isExist(CARTS, ORDERER_ID, ANOTHER_ITEM_ID));
            Assertions.assertTrue(service.isExist(CARTS, ORDERER_ID, THE_OTHER_ITEM_ID));

        }
        catch (Exception e)
        {
            //       e.printStackTrace();
        }

    }
}
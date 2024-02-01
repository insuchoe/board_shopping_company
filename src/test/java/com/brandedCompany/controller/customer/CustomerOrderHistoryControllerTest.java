package com.brandedCompany.controller.customer;

import com.brandedCompany.serivce.PagingAndSortingService;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.math.BigInteger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/root-context.xml"})
@Sql({"classpath:schema/brandedCompany_truncate.sql", "classpath:schema/brandedCompany_data.sql"})
public class CustomerOrderHistoryControllerTest
{
    private MockMvc mvc;
    @Autowired private CustomerOrderHistoryController controller;
    @Qualifier("pagingAndSortingServiceImpl") @Autowired private PagingAndSortingService service;


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

    @DisplayName("49번 고객은 주문내역 페이지 요청")
    @Test
    public void page1OfCustomerId49()
    {
        final BigInteger ORDERER_ID = BigInteger.valueOf(49);
        try
        {
            mvc.perform(get("/customer/" + ORDERER_ID + "/orderHistory?page=1"))
               .andExpect(status().isOk())
               .andExpect(view().name("customerOrderHistory"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}
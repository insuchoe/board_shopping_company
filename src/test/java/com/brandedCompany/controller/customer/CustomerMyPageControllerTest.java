package com.brandedCompany.controller.customer;

import com.brandedCompany.serivce.CRUDService;
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
public class CustomerMyPageControllerTest
{
    private MockMvc mvc;
    @Autowired CustomerMyPageController myPageCon;
    @Qualifier("CRUDServiceImpl") @Autowired CRUDService service;


    @Before
    public void setUp()
    {
        InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();

        internalResourceViewResolver.setPrefix("/WEB-INF/views/");
        internalResourceViewResolver.setSuffix(".jsp");
        mvc = MockMvcBuilders.standaloneSetup(myPageCon)
                             .setViewResolvers(internalResourceViewResolver)
                             .build();
    }

    @DisplayName("마이페이지로 이동")
    @Test
    public void goMyPage()
    {
        BigInteger customerId=BigInteger.valueOf(49);
        try
        {
            mvc.perform(get("/customer/"+49+"/myPage"))
               .andExpect(view().name("customerMyPage"))
                .andExpect(status().isOk());
        }
        catch (Exception e)
        {
            //         e.printStackTrace();
        }
    }
}


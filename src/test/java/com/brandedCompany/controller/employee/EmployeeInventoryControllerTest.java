package com.brandedCompany.controller.employee;

import com.brandedCompany.authentication.AuthenticationStatus;
import com.brandedCompany.authentication.EmployeeAuthentication;
import com.brandedCompany.authentication.EmployeeAuthenticationManager;
import com.brandedCompany.authentication.exception.AuthenticationNullPointerException;
import com.brandedCompany.authentication.exception.AuthenticationRuntimeException;
import com.brandedCompany.authentication.exception.EmployeeAuthenticationTargetNullPointerException;
import com.brandedCompany.authentication.interceptor.EmployeeLoginAuthenticationInterceptor;
import com.brandedCompany.config.MessageConfig;
import com.brandedCompany.domain.Employee;
import com.brandedCompany.serivce.CRUDService;
import com.brandedCompany.serivce.PagingAndSortingService;
import com.brandedCompany.validator.EmployeeLoginValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.servlet.http.Cookie;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/root-context.xml"})
@Sql({"classpath:schema/brandedCompany_truncate.sql", "classpath:schema/brandedCompany_data.sql"})
public class EmployeeInventoryControllerTest
{
    private MockMvc mvc;
    @Autowired EmployeeInventoryController controller;
    @Autowired EmployeeAuthenticationManager manager;
    @Qualifier("pagingAndSortingServiceImpl") @Autowired PagingAndSortingService service;
    @Autowired private EmployeeLoginAuthenticationInterceptor authInterceptor;

    final BigInteger EMPLOYEE_ID = BigInteger.valueOf(49);
    final String FIRST_NAME = "Isabella";
    final String LAST_NAME = "Cole";

    @Before
    public void setUp()
    {
        InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();

        internalResourceViewResolver.setPrefix("/WEB-INF/views/");
        internalResourceViewResolver.setSuffix(".jsp");
        mvc = MockMvcBuilders.standaloneSetup(controller)
                             .setViewResolvers(internalResourceViewResolver)
                             .addInterceptors(authInterceptor)
                             .build();
        try
        {
            manager.addAuthentication(new EmployeeAuthentication(new Employee(EMPLOYEE_ID, FIRST_NAME, LAST_NAME)));
        }
        catch (AuthenticationRuntimeException e)
        {
            e.printStackTrace();
        }
    }

    @DisplayName("49번 직원은 인벤토리 페이지 요청한다.")
    @Test
    public void goToInventoryPage()
    {
        try
        {
            //when
            mvc.perform(get("/employee/"+EMPLOYEE_ID+"/inventory"))
                //then
                .andExpect(status().isOk())
                .andExpect(view().name("employeeInventory"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


}


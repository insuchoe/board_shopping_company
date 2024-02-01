package com.brandedCompany.controller.employee;

import com.brandedCompany.authentication.EmployeeAuthentication;
import com.brandedCompany.authentication.EmployeeAuthenticationManager;
import com.brandedCompany.authentication.exception.AuthenticationRuntimeException;
import com.brandedCompany.authentication.interceptor.EmployeeLoginAuthenticationInterceptor;
import com.brandedCompany.domain.Employee;
import com.brandedCompany.serivce.OptionServiceImpl;
import org.junit.Before;
import org.junit.Test;
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
public class EmployeeMyPageControllerTest
{
    @Autowired private EmployeeMyPageController controller;
    @Autowired private EmployeeLoginAuthenticationInterceptor employeeLoginAuthenticationInterceptor;
    @Autowired private EmployeeAuthenticationManager manager;

    private MockMvc mvc;
    @Qualifier("optionServiceImpl") @Autowired OptionServiceImpl service;

    final BigInteger EMPLOYEE_ID = BigInteger.valueOf(49);
    final String FIRST_NAME = "Isabella";
    final String LAST_NAME = "Cole";
    final String NAME = "Isabella Cole";

    @Before
    public void setUp()
    {
        InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();

        internalResourceViewResolver.setPrefix("/WEB-INF/views/");
        internalResourceViewResolver.setSuffix(".jsp");

        mvc = MockMvcBuilders.standaloneSetup(controller)
                             .addInterceptors(employeeLoginAuthenticationInterceptor)
                             .setViewResolvers(internalResourceViewResolver)
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

    @Test
    public void goToMyPage()
    {
        try
        {
            mvc.perform(get("/employee/49/myPage"))
                .andExpect(status().isOk())
                .andExpect(view().name("employeeMyPage"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }








}

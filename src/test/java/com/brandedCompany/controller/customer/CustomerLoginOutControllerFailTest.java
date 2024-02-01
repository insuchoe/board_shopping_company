package com.brandedCompany.controller.customer;

import com.brandedCompany.authentication.AuthenticationStatus;
import com.brandedCompany.authentication.CustomerAuthentication;
import com.brandedCompany.authentication.CustomerAuthenticationManager;
import com.brandedCompany.authentication.exception.AuthenticationNotFoundException;
import com.brandedCompany.authentication.exception.AuthenticationNullPointerException;
import com.brandedCompany.authentication.exception.AuthenticationRuntimeException;
import com.brandedCompany.authentication.exception.CustomerAuthenticationTargetNullPointerException;
import com.brandedCompany.authentication.interceptor.CustomerLoginAuthenticationInterceptor;
import com.brandedCompany.config.MessageConfig;
import com.brandedCompany.domain.Customer;
import com.brandedCompany.validator.CustomerLoginValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CustomerLoginOutControllerFailTest
{
    private MockMvc mvc;
    @Autowired CustomerLoginOutController myPagC;
    @Autowired CustomerAuthenticationManager manager;

    @Autowired MessageSourceAccessor messageSourceAccessor;
    @Autowired MessageConfig messageConfig;
    @Autowired private CustomerLoginAuthenticationInterceptor authInterceptor;
    @Autowired CustomerLoginValidator loginValidator;

    @Before
    public void setUp()
    {
        InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();

        internalResourceViewResolver.setPrefix("/WEB-INF/views/");
        internalResourceViewResolver.setSuffix(".jsp");
        mvc = MockMvcBuilders.standaloneSetup(myPagC)
                             .setViewResolvers(internalResourceViewResolver)
                             .setValidator(loginValidator)
                             .addInterceptors(authInterceptor)
                             .build();

        final BigInteger ID = BigInteger.valueOf(49);
        if (manager.hasAuthentication(ID))
        {
            try
            {
                manager.removeAuthentication(manager.getAuthentication(ID));
            }
            catch (AuthenticationNotFoundException | AuthenticationNullPointerException e)
            {
                //    e.printStackTrace();
            }
        }
    }

    @DisplayName("고객이 아닌 정보로 로그인")
    @Test
    public void logInWithNonCustomerInformation()
    {
        // given
        final String ERROR_CODE = "notFound.customer";
        final BigInteger ID = BigInteger.valueOf(99999);
        final String NAME = "unknown kim";
        final Customer customer = new Customer(ID, NAME);
        try
        {
            // when
            // 로그인
            mvc.perform(post("/customer/loginOut/login").param("customerId", String.valueOf(customer.getCustomerId()))
                                                        .param("name", customer.getName()))
               // then
               // 상태코드 - 4XX(=클라이언트 에러)
               // 뷰 - customerLogin(=로그인 페이지)
               // 에러 메세지 - notFound.customer(=고객을 찾지 못했습니다 !)
               // 고객인증 - false(=로그인 실패)
               .andExpect(status().is4xxClientError())
               .andExpect(view().name("customerLogin"))
               .andExpect(model().size(1))
               .andExpect(model().attribute("cusLgnErr", ERROR_CODE))
               .andExpect(mvcResult -> assertFalse(manager.hasAuthentication(ID)));
        }
        catch (Exception e)
        {
            //   e.printStackTrace();
        }
    }

    @DisplayName("아이디를 미입력하고 로그인")
    @Test
    public void loginWithOutId()
    {
        // given
        final BigInteger ID = null;
        final String NAME = "Kym Hawken";
        final String ERROR_CODE = "required.customer.customerId";
        try
        {
            // when
            // 로그인
            mvc.perform(post("/customer/loginOut/login").param("customerId", String.valueOf(ID))
                                                        .param("name", NAME))
               // then
               // 상태코드 - 4XX(=클라이언트 에러)
               // 뷰 - customerLogin(=로그인 페이지)
               // 에러 메세지 - required.customer.customerId(=고객번호를 숫자로 입력해주세요 !)
               // 고객인증 - false(=로그인 실패)
               .andExpect(status().is4xxClientError())
               .andExpect(view().name("customerLogin"))
               .andExpect(model().size(1))
               .andExpect(model().attribute("cusLgnErr", ERROR_CODE))
               .andExpect(mvcResult -> assertFalse(manager.hasAuthentication(ID)));
        }
        catch (Exception e)
        {
            //           e.printStackTrace();
        }
    }

    @DisplayName("아이디와 이름을 공백으로 입력하고 로그인")
    @Test
    public void loginWithBlankIdAndName()
    {
        // given
        final String ERROR_CODE = "required.customer.customerIdName";
        try
        {
            // when
            // 로그인
            mvc.perform(post("/customer/loginOut/login").param("customerId", "               ")
                                                        .param("name", "                     "))
               // then
               // 상태코드 - 4XX(=클라이언트 에러)
               // 뷰 - customerLogin(=로그인 페이지)
               // 에러 메세지 - required.customer.customerId(=고객번호와 고객이름을 입력해주세요 !)
               .andExpect(status().is4xxClientError())
               .andExpect(view().name("customerLogin"))
               .andExpect(model().size(1))
               .andExpect(model().attribute("cusLgnErr", ERROR_CODE));


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @DisplayName("이름을 공백으로 입력하고 로그인")
    @Test
    public void loginWithBlankName()
    {
        // given
        final String ERROR_CODE = "required.customer.name";
        final BigInteger ID = BigInteger.valueOf(49);
        try
        {
            // when
            // 로그인
            mvc.perform(post("/customer/loginOut/login").param("customerId", String.valueOf(ID))
                                                        .param("name", "                       "))
               // then
               // 상태코드 - 4XX(=클라이언트 에러)
               // 뷰 - customerLogin(=로그인 페이지)
               // 에러 메세지 - required.customer.customerId(=고객이름을 입력해주세요 !)
               .andExpect(status().is4xxClientError())
               .andExpect(view().name("customerLogin"))
               .andExpect(model().size(1))
               .andExpect(model().attribute("cusLgnErr", ERROR_CODE));


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @DisplayName("아이디 공백으로 입력하고 로그인")
    @Test
    public void loginWithBlankId()
    {
        // given
        final String ERROR_CODE = "required.customer.customerId";
        final String NAME = "Kym Hawken";
        try
        {
            // when
            // 로그인
            mvc.perform(post("/customer/loginOut/login").param("customerId", "                   ")
                                                        .param("name", NAME))
               // then
               // 상태코드 - 4XX(=클라이언트 에러)
               // 뷰 - customerLogin(=로그인 페이지)
               // 에러 메세지 - required.customer.customerId(=고객 번호를 숫자로 입력해주세요 !)
               .andExpect(status().is4xxClientError())
               .andExpect(view().name("customerLogin"))
               .andExpect(model().size(1))
               .andExpect(model().attribute("cusLgnErr", ERROR_CODE));


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @DisplayName("아이디를 문자로 입력하고 로그인")
    @Test
    public void loginWithInvalidIdFormat()
    {
        // given
        final String ERROR_CODE = "required.customer.customerId";
        final String NAME = "Kym Hawken";
        try
        {
            // when
            // 로그인
            mvc.perform(post("/customer/loginOut/login").param("customerId", "unknwon")
                                                        .param("name", NAME))
               // then
               // 상태코드 - 4XX(=클라이언트 에러)
               // 뷰 - customerLogin(=로그인 페이지)
               // 에러 메세지 - required.customer.customerId(=고객 번호를 숫자로 입력해주세요 !)
               .andExpect(status().is4xxClientError())
               .andExpect(view().name("customerLogin"))
               .andExpect(model().size(1))
               .andExpect(model().attribute("cusLgnErr", ERROR_CODE));


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @DisplayName("이름을 숫자로 입력하고 로그인")
    @Test
    public void loginWithInvalidNameFormat()
    {
        // given
        final BigInteger ID = BigInteger.valueOf(49);
        final String NAME = "1111";
        final String ERROR_CODE = "typeMismatch.customer.name";

        try
        {
            // when
            // 로그인
            mvc.perform(post("/customer/loginOut/login").param("customerId", String.valueOf(ID))
                                                        .param("name", NAME))
               // then
               // 상태코드 - 400(=클라이언트 에러)
               // 에러 메세지 - typeMismatch.customer.name(=고객 이름을 문자로 입력해주세요 !)
               // 고객인증 - false(=로그인 실패)
               .andExpect(status().is4xxClientError())
               .andExpect(view().name("customerLogin"))
               .andExpect(model().size(1))
               .andExpect(model().attribute("cusLgnErr", ERROR_CODE))
               .andExpect(mvcResult -> assertFalse(manager.hasAuthentication(ID)));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @DisplayName("이름을 미입력하고 로그인")
    @Test
    public void loginWithoutEnteringName()
    {
        // given
        final String ERROR_CODE = "required.customer.name";
        final BigInteger ID = BigInteger.valueOf(49);
        final String NAME = "";
        try
        {
            // when
            // 로그인
            mvc.perform(post("/customer/loginOut/login").param("customerId", String.valueOf(ID))
                                                        .param("name", NAME))
               // then
               // 상태코드 - 4XX(=클라이언트 에러)
               // 뷰 - customerLogin(=로그인 페이지)
               // 에러 메세지 -required.customer.name(=고객이름을 입력해주세요 !)
               // 고객인증 - false(=로그인 실패)
               .andExpect(status().is4xxClientError())
               .andExpect(view().name("customerLogin"))
               .andExpect(model().size(1))
               .andExpect(model().attribute("cusLgnErr", ERROR_CODE))
               .andExpect(mvcResult -> assertFalse(manager.hasAuthentication(ID)));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @DisplayName("아이디와 이름을 입력하지 않고 로그인")
    @Test
    public void loginWithUnenteredData()
    {
        // given
        final String ERROR_CODE = "required.customer.customerIdName";
        try
        {
            // when
            // 로그인
            mvc.perform(post("/customer/loginOut/login").param("customerId", "")// 잘못 입력(에러 유도)
                                                        .param("name", "")) // 잘못 입력(에러 유도)
               // then
               // 상태코드 - 4XX(=클라이언트 에러)
               // 뷰 - customerLogin(=로그인 페이지)
               // 에러 메세지 - required.customer.customerIdName(=고객번호와 고객이름을 입력해주세요 !)
               .andExpect(status().is4xxClientError())
               .andExpect(view().name("customerLogin"))
               .andExpect(model().size(1))
               .andExpect(model().attribute("cusLgnErr", ERROR_CODE));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    


}


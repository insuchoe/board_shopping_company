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
public class CustomerLoginOutControllerTest
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
                //            e.printStackTrace();
            }
        }
    }

    @DisplayName("로그인 페이지 요청")
    @Test
    public void requestLoginPage()
    {
        try
        {
            // when
            // 로그인 페이지 요청
            mvc.perform(get("/customer/loginOut/login"))
               // then
               // 뷰 - customerLogin(=로그인 페이지)
               // 상태코드 - 200
               .andExpect(view().name("customerLogin"))
               .andExpect(status().isOk());

        }
        catch (Exception e)
        {
            //      e.printStackTrace();
        }
    }

    @DisplayName("로그인")
    @Test
    public void login()
    {
        // given
        final BigInteger ID = BigInteger.valueOf(49);
        final String NAME = "Kym Hawken";
        try
        {
            // when
            // 로그인
            mvc.perform(post("/customer/loginOut/login").param("customerId", String.valueOf(ID))
                                                        .param("name", NAME))
               // then
               // 상태코드 - 302
               // 리다이렉트 경로 - /customer/49/myPage(=프로필 페이지)
               // 고객인증 - ture(=로그인 성공)
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/customer/" + ID + "/myPage"))
               .andExpect(mvcResult ->
                          {
                              try
                              {
                                  assertTrue(manager.hasAuthentication(ID));
                                  CustomerAuthentication authentication = (CustomerAuthentication)manager.getAuthentication(ID);
                                  assertEquals(AuthenticationStatus.SUCCESS, authentication.getStatus());
                              }
                              catch (AuthenticationNullPointerException e)
                              {
                                  //                               e.printStackTrace();
                              }
                          })
               .andDo(print());

        }
        catch (Exception e)
        {
            //     e.printStackTrace();
        }

    }

    @DisplayName("로그아웃")
    @Test
    public void logout()
    {
        try
        {
            // given
            final BigInteger ID = BigInteger.valueOf(49);
            final String NAME = "Kym Hawken";
            CustomerAuthentication authentication = new CustomerAuthentication(new Customer(ID, NAME));
            mvc.perform(servletContext ->
                        {
                            try
                            {
                                manager.addAuthentication(authentication);
                            }
                            catch (Exception | AuthenticationRuntimeException e)
                            {
                                //               e.printStackTrace();
                            }
                            servletContext.setAttribute("customer", new Customer(ID, NAME)); // 로그인 성공 의미
                            // when
                            // 로그아웃
                            return get("/customer/" + ID + "/loginOut/logout").buildRequest(servletContext);
                        })
               .andDo(print())
               // then
               // 상태 코드 - 302
               // 뷰 - 로그인 페이지
               // 고객인증 - false(=로그아웃)
               .andExpect(status().is(302))
               .andExpect(redirectedUrl("/customer/loginOut/login"))
               .andExpect(mvcResult -> assertFalse(manager.hasAuthentication(ID)))
               .andDo(print());
        }
        catch (Exception | CustomerAuthenticationTargetNullPointerException e)
        {
            //     e.printStackTrace();
        }
    }

    @DisplayName("아이디 기억하기를 체크한 후 로그인")
    @Test
    public void loginWithRememberIdChecked()
    {
        // given
        final BigInteger ID = BigInteger.valueOf(49);
        final String NAME = "Kym Hawken";
        final boolean REMEMBER_ID = true;
        try
        {

            // when
            // 로그인
            mvc.perform(post("/customer/loginOut/login").param("customerId", String.valueOf(ID))
                                                        .param("name", NAME)
                                                        .param("rememberId", String.valueOf(REMEMBER_ID)))
               // then
               // 쿠키 - {"rememberCustomerId", 49, 86400}
               // 리다이렉트 경로 - /customer/49/myPage(=마이 페이지)
               // 상태코드 - 302
               // 고객인증 - true(=로그인 성공)
               .andExpect(cookie().exists("rememberCustomerId"))
               .andExpect(cookie().value("rememberCustomerId", String.valueOf(ID)))
               .andExpect(cookie().maxAge("rememberCustomerId", 86400))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/customer/" + ID + "/myPage"))
               .andExpect(mvcResult -> assertTrue(manager.hasAuthentication(ID)))
               .andDo(print());
        }
        catch (Exception e)
        {
            //    e.printStackTrace();
        }
    }

    @DisplayName("아이디 기억하기를 체크한 후 로그아웃")
    @Test
    public void logoutWithRememberIdChecked()
    {
        // given
        final BigInteger ID = BigInteger.valueOf(49);
        final String NAME = "Kym Hawken";
        try
        {
            MockHttpServletRequestBuilder requestBuilder = get("/customer/" + ID + "/loginOut/logout");
            String cookieName = "rememberId";
            String cookieValue = String.valueOf(ID);
            Cookie cookie = new Cookie(cookieName, cookieValue);
            cookie.setMaxAge(86400);

            InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();

            mvc = MockMvcBuilders.standaloneSetup(myPagC)
                                 .setViewResolvers(internalResourceViewResolver)
                                 .addInterceptors(authInterceptor)
                                 //                                 .setControllerAdvice(customerLoginOutCatcher)
                                 .defaultRequest(requestBuilder.cookie(cookie))
                                 .build();


            Optional<Cookie> rememberId = Arrays.stream(Objects.requireNonNull(mvc.perform(servletContext ->
                                                                                           {
                                                                                               try
                                                                                               {
                                                                                                   manager.addAuthentication(new CustomerAuthentication(new Customer(ID, NAME)));
                                                                                               }
                                                                                               catch (AuthenticationRuntimeException e)
                                                                                               {
                                                                                                   //                                                                                         e.printStackTrace();
                                                                                               }
                                                                                               // when
                                                                                               // 로그아웃
                                                                                               return requestBuilder.buildRequest(servletContext);
                                                                                           })
                                                                                  .andReturn()
                                                                                  .getRequest()
                                                                                  .getCookies()))
                                                .filter(cookie1 -> cookieName.equals(cookie1.getName()))
                                                .findAny();
            // then
            // 쿠키 value - 49
            // 고객인증 - false(=로그아웃 상태)
            assertTrue(rememberId.isPresent());
            BigInteger rememberIdValue = BigInteger.valueOf(Long.parseLong(rememberId.get()
                                                                                     .getValue()));
            assertEquals(cookieValue, String.valueOf(rememberIdValue));
            assertFalse(manager.hasAuthentication(rememberIdValue));
        }
        catch (Exception e)
        {
            //  e.printStackTrace();
        }
    }




}


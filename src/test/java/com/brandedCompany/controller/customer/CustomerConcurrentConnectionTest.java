package com.brandedCompany.controller.customer;

import com.brandedCompany.authentication.CustomerAuthentication;
import com.brandedCompany.authentication.CustomerAuthenticationManager;
import com.brandedCompany.authentication.exception.AuthenticationRuntimeException;
import com.brandedCompany.authentication.exception.CustomerAuthenticationTargetNullPointerException;
import com.brandedCompany.authentication.interceptor.CustomerLoginAuthenticationInterceptor;
import com.brandedCompany.concurrentConnection.CustomerConcurrentConnection;
import com.brandedCompany.concurrentConnection.CustomerConcurrentConnectionManager;
import com.brandedCompany.concurrentConnection.CustomerConcurrentConnectionObserver;
import com.brandedCompany.concurrentConnection.exception.ConcurrentConnectionRuntimeException;
import com.brandedCompany.config.MessageConfig;
import com.brandedCompany.domain.Customer;
import com.brandedCompany.concurrentConnection.catcher.CustomerConcurrentConnectionObserverCatcher;
import com.brandedCompany.serivce.CRUDService;
import com.brandedCompany.validator.CustomerLoginValidator;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.math.BigInteger;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/root-context.xml"})
@Sql({"classpath:schema/brandedCompany_truncate.sql", "classpath:schema/brandedCompany_data.sql"})
public class CustomerConcurrentConnectionTest
{
    private MockMvc mvc;
    @Autowired CustomerLoginOutController customerLoginOutController;
    @Autowired CustomerAuthenticationManager authManager;
    @Autowired CustomerConcurrentConnectionManager conManager;
    @Autowired CustomerConcurrentConnectionObserver observer;
    @Qualifier("CRUDServiceImpl") @Autowired CRUDService service;
    @Autowired CustomerConcurrentConnectionObserverCatcher catcher;
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
        mvc = MockMvcBuilders.standaloneSetup(customerLoginOutController)
                             .setViewResolvers(internalResourceViewResolver)
                             .setValidator(loginValidator)
                             .addInterceptors(authInterceptor)
                             .setControllerAdvice(catcher)
                             .build();
    }

    @DisplayName("이미 로그인한 계정으로 로그인")
    @Test
    public void logInWithAccountLoggedAlready()
    {
        final BigInteger ID = BigInteger.valueOf(49);
        final String NAME = "Kym Hawken";
        try
        {
            CustomerAuthentication authentication = new CustomerAuthentication(new Customer(ID, NAME));
            mvc.perform(servletContext ->
                        {
                            try
                            {
                                // given
                                // 인증 성공(=접속중)
                                authManager.addAuthentication(authentication);
                            }
                            catch (AuthenticationRuntimeException | Exception e)
                            {
                                //     e.printStackTrace();
                            }

                            // given
                            // 로그인 시도
                            return MockMvcRequestBuilders.post("/customer/loginOut/login")
                                                         .param("customerId", String.valueOf(ID))
                                                         .param("name", NAME)
                                                         .buildRequest(servletContext);
                        })
               // then
               // 상태코드 - 423
               // 뷰 - customerLogin(=로그인 페이지)
               // 에러 메세지 - customer.alreadyAccessor(=이미 접속중입니다)
               // 동시접속 횟수 - 1
               .andExpect(status().isLocked())
               .andExpect(MockMvcResultMatchers.view()
                                               .name("customerLogin"))
               .andExpect(model().size(1))
               .andExpect(model().attribute("cusLgnErr", "customer.concurrentConnection"))
               .andExpect(mvcResult -> conManager.has(authentication))
               .andExpect(mvcResult ->
                          {
                              try
                              {
                                  CustomerConcurrentConnection concurrentConnection = (CustomerConcurrentConnection) conManager.get(authentication);
                                  assertEquals(1, concurrentConnection.getCount());
                              }
                              catch (AuthenticationRuntimeException | ConcurrentConnectionRuntimeException e)
                              {
                                  e.printStackTrace();
                              }

                          });
        }
        catch (Exception | CustomerAuthenticationTargetNullPointerException e)
        {
            e.printStackTrace();
        }
    }


    @DisplayName("동시접속 2회중인 계정으로 로그인")
    @Test
    public void logInToAccountThatHasTwoConcurrentConnectionCount()
    {
        BigInteger ID = BigInteger.valueOf(49);
        final String NAME = "Kym Hawken";
        try
        {
            CustomerAuthentication authentication = new CustomerAuthentication(new Customer(ID, NAME));
            mvc.perform(servletContext ->
                        {
                            try
                            {
                                // 인증 성공(=로그인)
                                authManager.addAuthentication(authentication);
                                // given
                                // 동시접속 2회
                                IntStream.range(0, 2)
                                         .forEach(value ->
                                                  {
                                                      try
                                                      {
                                                          observer.update(authentication);
                                                      }
                                                      catch (AuthenticationRuntimeException | ConcurrentConnectionRuntimeException e)
                                                      {
                                                          e.printStackTrace();
                                                      }
                                                  });
                            }
                            catch (AuthenticationRuntimeException | Exception e)
                            {
                                e.printStackTrace();
                            }

                            // when
                            // 로그인
                            return MockMvcRequestBuilders.post("/customer/loginOut/login")
                                                         .param("customerId", String.valueOf(ID))
                                                         .param("name", NAME)
                                                         .buildRequest(servletContext);
                        })
               // then
               // 상태코드 - 423
               // 뷰 - concurrentConnectionExceed(=동시 접속 경고 페이지)
               // 에러 메세지 - none
               // 동시접속 횟수 - 3
               .andExpect(status().isLocked())
               .andExpect(MockMvcResultMatchers.view()
                                               .name("concurrentConnectionExceed"))
               .andExpect(model().size(0))
               .andExpect(model().attributeDoesNotExist("cusLgnErr"))
               .andExpect(mvcResult -> conManager.has(authentication))
               .andExpect(mvcResult ->
                          {
                              try
                              {
                                  CustomerConcurrentConnection concurrentConnection = (CustomerConcurrentConnection) conManager.get(authentication);
                                  assertEquals(3, concurrentConnection.getCount());
                              }
                              catch (AuthenticationRuntimeException | ConcurrentConnectionRuntimeException e)
                              {
                                  e.printStackTrace();
                              }
                          })
               .andDo(print());
        }
        catch (Exception | CustomerAuthenticationTargetNullPointerException e)
        {
            e.printStackTrace();
        }
    }

    @DisplayName("동시접속 2회중인 계정으로 로그아웃")
    @Test
    public void logOutWithAccountThatHasTwoConcurrentConnectionCount() throws CustomerAuthenticationTargetNullPointerException
    {
        final BigInteger ID = BigInteger.valueOf(49);
        final String NAME = "Kym Hawken";
        CustomerAuthentication authentication = new CustomerAuthentication(new Customer(ID, NAME));
        try
        {
            mvc.perform(servletContext ->
                        {
                            try
                            {
                                // 인증 성공(=로그인)
                                authManager.addAuthentication(authentication);
                                // given
                                // 동시접속 2회
                                IntStream.range(0, 2)
                                         .forEach(value ->
                                                  {
                                                      try
                                                      {
                                                          observer.update(authentication);
                                                      }
                                                      catch (AuthenticationRuntimeException | ConcurrentConnectionRuntimeException e)
                                                      {
                                                          e.printStackTrace();
                                                      }
                                                  });
                            }
                            catch (Exception | AuthenticationRuntimeException e)
                            {
                                e.printStackTrace();
                            }
                            // when
                            // 로그아웃
                            return get("/customer/" + ID + "/loginOut/logout").buildRequest(servletContext);
                        })
               // then
               // 상태코드 - 200
               // 뷰 - customerLogin(=로그인 페이지)
               // 에러메시지 - none
               // 동시접속 횟수 - none
               .andExpect(status().is(302))
               .andExpect(redirectedUrl("/customer/loginOut/login"))
               .andExpect(model().size(0))
               .andExpect(model().attributeDoesNotExist("cusLgnErr"))
               .andExpect(mvcResult -> assertFalse(conManager.has(authentication)))
               .andDo(print());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @DisplayName("동시접속 2회중인 계정으로 로그아웃 후 다시 로그인")
    @Test
    public void logOutAndLoginAgainWithAccountThatHasBeenConcurrentConnectionTwiceAtTheSameTime()
    {
        final BigInteger ID = BigInteger.valueOf(49);
        final String NAME = "Kym Hawken";
        try
        {
            Customer customer = new Customer(ID, NAME);
            CustomerAuthentication authentication = new CustomerAuthentication(customer);
            mvc.perform(servletContext ->
                        {
                            try
                            {
                                // 인증 성공(=로그인)

                                authManager.addAuthentication(authentication);
                                // given
                                // 동시접속 2회
                                IntStream.range(0, 2)
                                         .forEach(value ->
                                                  {
                                                      try
                                                      {
                                                          observer.update(authentication);
                                                      }
                                                      catch (AuthenticationRuntimeException | ConcurrentConnectionRuntimeException e)
                                                      {
                                                          e.printStackTrace();
                                                      }
                                                  });
                            }
                            catch (Exception | AuthenticationRuntimeException e)
                            {
                                e.printStackTrace();
                            }
                            // when
                            // 로그아웃 후 다시 로그인
                            return get("/customer/" + ID + "/loginOut/logout").buildRequest(servletContext);
                        })
               .andDo(mvcResult -> mvc.perform(post("/customer/loginOut/login").param("customerId", String.valueOf(ID))
                                                                               .param("name", NAME))
                                      // then
                                      // 상태코드 - 302
                                      // 리다이렉트 경로 - /customer/49/myPage(=프로필 페이지)
                                      // 에러메시지 - none
                                      // 동시접속 횟수 - none
                                      .andExpect(status().is3xxRedirection())
                                      .andExpect(MockMvcResultMatchers.redirectedUrl("/customer/" + ID + "/myPage"))
                                      .andExpect(model().attributeDoesNotExist("cusLgnErr"))
                                      .andExpect(mvcResult2 -> assertFalse(conManager.has(authentication)))
                                      .andDo(print()));
        }
        catch (Exception | CustomerAuthenticationTargetNullPointerException e)
        {
            e.printStackTrace();
        }
    }


}


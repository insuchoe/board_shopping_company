package com.brandedCompany.controller.employee;

import com.brandedCompany.authentication.Authentication;
import com.brandedCompany.authentication.EmployeeAuthentication;
import com.brandedCompany.authentication.EmployeeAuthenticationManager;
import com.brandedCompany.authentication.exception.AuthenticationNotFoundException;
import com.brandedCompany.authentication.exception.AuthenticationNullPointerException;
import com.brandedCompany.authentication.exception.AuthenticationRuntimeException;
import com.brandedCompany.authentication.exception.EmployeeAuthenticationTargetNullPointerException;
import com.brandedCompany.authentication.interceptor.EmployeeLoginAuthenticationInterceptor;
import com.brandedCompany.concurrentConnection.ConcurrentConnection;
import com.brandedCompany.concurrentConnection.EmployeeConcurrentConnectionManager;
import com.brandedCompany.concurrentConnection.EmployeeConcurrentConnectionObserver;
import com.brandedCompany.concurrentConnection.catcher.EmployeeConcurrentConnectionObserverCatcher;
import com.brandedCompany.concurrentConnection.exception.ConcurrentConnectionRuntimeException;
import com.brandedCompany.config.MessageConfig;
import com.brandedCompany.domain.Employee;
import com.brandedCompany.serivce.CRUDService;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.math.BigInteger;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/root-context.xml"})
@Sql({"classpath:schema/brandedCompany_truncate.sql", "classpath:schema/brandedCompany_data.sql"})
public class EmployeeConcurrentConnectionTest
{
    private MockMvc mvc;
    @Autowired EmployeeLoginOutController employeeLoginOutController;
    @Autowired EmployeeAuthenticationManager authManager;
    @Autowired EmployeeConcurrentConnectionManager conManager;
    @Autowired EmployeeConcurrentConnectionObserver observer;
    @Qualifier("CRUDServiceImpl") @Autowired CRUDService service;
    @Autowired EmployeeConcurrentConnectionObserverCatcher catcher;
    @Autowired MessageSourceAccessor messageSourceAccessor;
    @Autowired MessageConfig messageConfig;
    @Autowired private EmployeeLoginAuthenticationInterceptor authInterceptor;
    @Autowired EmployeeLoginValidator loginValidator;

    @Before
    public void setUp()
    {
        InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();

        internalResourceViewResolver.setPrefix("/WEB-INF/views/");
        internalResourceViewResolver.setSuffix(".jsp");
        mvc = MockMvcBuilders.standaloneSetup(employeeLoginOutController)
                             .setViewResolvers(internalResourceViewResolver)
                             .setValidator(loginValidator)
                             .addInterceptors(authInterceptor)
                             .setControllerAdvice(catcher)
                             .build();

        try
        {

            if (authManager.hasAuthentication(BigInteger.valueOf(49)))
            {
                Authentication authentication = authManager.getAuthentication(BigInteger.valueOf(49));
                authManager.removeAuthentication(authentication);
                if (conManager.has(authentication))
                    conManager.remove(authentication);
            }

        }
        catch (AuthenticationNullPointerException | ConcurrentConnectionRuntimeException | AuthenticationNotFoundException e)
        {
            //   e.printStackTrace();
        }
    }

    @DisplayName("이미 로그인한 계정으로 로그인_동시접속 횟수 1회")
    @Test
    public void logInWithAccountLoggedAlready_concurrentConnectionCountOne()
    {
        final BigInteger ID = BigInteger.valueOf(49);
        final String FIRST_NAME = "Isabella";
        final String LAST_NAME = "Cole";
        try
        {
            EmployeeAuthentication authentication = new EmployeeAuthentication(new Employee(ID, FIRST_NAME, LAST_NAME));
            mvc.perform(servletContext ->
                        {
                            try
                            {
                                // given
                                // 인증성공(=접속중)
                                authManager.addAuthentication(authentication);
                            }
                            catch (AuthenticationRuntimeException | Exception e)
                            {
                                //                      e.printStackTrace();
                            }
                            // when
                            // 로그인 시도
                            return post("/employee/loginOut/login").param("employeeId", String.valueOf(ID))
                                                                   .param("firstName", FIRST_NAME)
                                                                   .param("lastName", LAST_NAME)
                                                                   .buildRequest(servletContext);
                        })
               // then
               // 상태코드 - 423
               // 뷰 - concurrentConnectionExceed(=동시접속 오류 페이지)
               // 동시접속 횟수 - 1
               .andExpect(status().isLocked())
               .andExpect(view().name("concurrentConnectionExceed"))
               .andExpect(model().attributeDoesNotExist("empLgnErr"))
               .andExpect(mvcResult ->
                          {
                              try
                              {
                                  assertTrue(conManager.has(authentication));
                                  ConcurrentConnection concurrentConnection = conManager.get(authentication);
                                  assertEquals(1, concurrentConnection.getCount());
                              }
                              catch (AuthenticationRuntimeException | ConcurrentConnectionRuntimeException e)
                              {
                                  //                          e.printStackTrace();
                              }

                          });
        }
        catch (Exception | EmployeeAuthenticationTargetNullPointerException e)
        {
            //   e.printStackTrace();
        }
    }


    @DisplayName("동시접속 2회중인 계정으로 로그아웃_동시접속 이력 없음")
    @Test
    public void logOutWithAccountThatHasTwoConcurrentConnectionCount_isNullConcurrentConnection() throws EmployeeAuthenticationTargetNullPointerException
    {
        final BigInteger ID = BigInteger.valueOf(49);
        final String FIRST_NAME = "Isabella";
        final String LAST_NAME = "Cole";
        EmployeeAuthentication authentication = new EmployeeAuthentication(new Employee(ID, FIRST_NAME, LAST_NAME));
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
                                                          //                   e.printStackTrace();
                                                      }
                                                  });
                            }
                            catch (Exception | AuthenticationRuntimeException e)
                            {
                                //       e.printStackTrace();
                            }
                            // when
                            // 로그아웃
                            return get("/employee/" + ID + "/loginOut/logout").buildRequest(servletContext);
                        })
               // then
               // 상태코드 - 200
               // 뷰 - employeeLogin(=로그인 페이지)
               // 에러메시지 - none
               // 동시접속 횟수 - none
               .andExpect(status().is(302))
               .andExpect(redirectedUrl("/employee/loginOut/login"))
               .andExpect(model().size(0))
               .andExpect(model().attributeDoesNotExist("empLgnErr"))
               .andExpect(mvcResult -> assertFalse(conManager.has(authentication)))
               .andDo(print());
        }
        catch (Exception e)
        {
            //  e.printStackTrace();
        }
    }




}


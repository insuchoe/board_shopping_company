package com.brandedCompany.controller.employee;

import com.brandedCompany.advice.EmployeeControllerGlobalCatcher;
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
public class EmployeeLoginOutControllerTest
{
    private MockMvc mvc;
    @Autowired EmployeeLoginOutController employeeLoginOutController;
    @Autowired EmployeeAuthenticationManager manager;
    @Qualifier("CRUDServiceImpl") @Autowired CRUDService service;
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
                             .build();

        final BigInteger ID = BigInteger.valueOf(49);
        if (manager.hasAuthentication(ID))
        {
            try
            {
                manager.removeAuthenticationById(ID);
            }
            catch (AuthenticationNullPointerException e)
            {
                //       e.printStackTrace();
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
            mvc.perform(get("/employee/loginOut/login"))
               // then
               // 뷰 - employeeLogin(=로그인 페이지)
               // 상태코드 - 200
               .andExpect(view().name("employeeLogin"))
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
        final String LAST_NAME = "Cole";
        final String FIRST_NAME = "Isabella";
        try
        {
            // when
            // 로그인
            mvc.perform(post("/employee/loginOut/login").param("employeeId", String.valueOf(ID))
                                                        .param("firstName", FIRST_NAME)
                                                        .param("lastName", LAST_NAME))
                // then
                // 상태코드 - 302
                // 리다이렉트 경로 - /employee/49/myPage(=프로필 페이지)
                // 직원인증 - ture(=로그인 성공)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employee/" + ID + "/myPage"))
                .andExpect(mvcResult ->
                           {
                               EmployeeAuthentication authentication = null;
                               try
                               {
                                   authentication = (EmployeeAuthentication) manager.getAuthentication(ID);
                                   assertTrue(manager.hasAuthentication(ID));
                                   assertEquals(AuthenticationStatus.SUCCESS, authentication.getStatus());
                               }
                               catch (AuthenticationNullPointerException e)
                               {
                                   //                          e.printStackTrace();
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
            final String LAST_NAME = "Cole";
            final String FIRST_NAME = "Isabella";
            EmployeeAuthentication authentication = new EmployeeAuthentication(new Employee(ID, FIRST_NAME, LAST_NAME));
            mvc.perform(servletContext ->
                        {
                            try
                            {
                                manager.addAuthentication(authentication);
                            }
                            catch (Exception | AuthenticationRuntimeException e)
                            {
                                //                e.printStackTrace();
                            }
                            servletContext.setAttribute("employee", new Employee(ID, FIRST_NAME,LAST_NAME)); // 로그인 성공 의미
                            // when
                            // 로그아웃
                            return get("/employee/" + ID + "/loginOut/logout").buildRequest(servletContext);
                        })
               .andDo(print())
               // then
               // 상태 코드 - 302
               // 뷰 - 로그인 페이지
               // 직원인증 - false(=로그아웃)
               .andExpect(status().is(302))
               .andExpect(redirectedUrl("/employee/loginOut/login"))
               .andExpect(mvcResult -> assertFalse(manager.hasAuthentication(ID)))
               .andDo(print());
        }
        catch (Exception | EmployeeAuthenticationTargetNullPointerException e)
        {
            //         e.printStackTrace();
        }
    }

    @DisplayName("아이디 기억하기를 체크한 후 로그인")
    @Test
    public void loginWithRememberIdChecked()
    {
        // given
        final BigInteger ID = BigInteger.valueOf(49);
        final String LAST_NAME = "Cole";
        final String FIRST_NAME = "Isabella";
        final boolean REMEMBER_ID = true;
        try
        {
            // when
            // 로그인
            mvc.perform(post("/employee/loginOut/login")
                                                .param("employeeId", String.valueOf(ID))
                                                .param("firstName", FIRST_NAME)
                                                .param("lastName",LAST_NAME)
                                                .param("rememberId", String.valueOf(REMEMBER_ID)))
               // then
               // 쿠키 - {"rememberEmployeeId", 49, 86400}
               // 리다이렉트 경로 - /employee/49/myPage(=마이 페이지)
               // 상태코드 - 302
               // 직원인증 - true(=로그인 성공)
               .andExpect(cookie().exists("rememberEmployeeId"))
               .andExpect(cookie().value("rememberEmployeeId", String.valueOf(ID)))
               .andExpect(cookie().maxAge("rememberEmployeeId", 86400))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/employee/" + ID + "/myPage"))
               .andExpect(mvcResult -> assertTrue(manager.hasAuthentication(ID)))
               .andDo(print());
        }
        catch (Exception e)
        {
            //         e.printStackTrace();
        }
    }

    @DisplayName("아이디 기억하기를 체크한 후 로그아웃")
    @Test
    public void logoutWithRememberIdChecked()
    {
        // given
        final BigInteger ID = BigInteger.valueOf(49);
        final String NAME="Cole Isabella";
        try
        {
            MockHttpServletRequestBuilder requestBuilder = get("/employee/" + ID + "/loginOut/logout");
            String cookieName = "rememberId";
            String cookieValue = String.valueOf(ID);
            Cookie cookie = new Cookie(cookieName, cookieValue);
            cookie.setMaxAge(86400);

            InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();

            mvc = MockMvcBuilders.standaloneSetup(employeeLoginOutController)
                                 .setViewResolvers(internalResourceViewResolver)
                                 .addInterceptors(authInterceptor)
                                 //                                 .setControllerAdvice(employeeLoginOutCatcher)
                                 .defaultRequest(requestBuilder.cookie(cookie))
                                 .build();


            Optional<Cookie> rememberId = Arrays.stream(Objects.requireNonNull(mvc.perform(servletContext ->
                                                                                           {
                                                                                               try
                                                                                               {
                                                                                                   manager.addAuthentication(new EmployeeAuthentication(new Employee(ID, NAME)));
                                                                                               }
                                                                                               catch (AuthenticationRuntimeException e)
                                                                                               {
                                                                                                   e.printStackTrace();
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
            // 직원인증 - false(=로그아웃 상태)
            assertTrue(rememberId.isPresent());
            BigInteger rememberIdValue = BigInteger.valueOf(Long.parseLong(rememberId.get()
                                                                                     .getValue()));
            assertEquals(cookieValue, String.valueOf(rememberIdValue));
            assertFalse(manager.hasAuthentication(rememberIdValue));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @DisplayName("직원이 아닌 정보로 로그인")
    @Test
    public void logInWithNonEmployeeInformation()
    {
        // given
        final String ERROR_CODE = "notFound.employee";
        final BigInteger ID = BigInteger.valueOf(99999);
        final String FIRST_NAME = "unknown";
        final String LAST_NAME = "kim";
        final Employee employee = new Employee(ID, FIRST_NAME,LAST_NAME);
        try
        {
            // when
            // 로그인
            mvc.perform(post("/employee/loginOut/login").param("employeeId", String.valueOf(employee.getEmployeeId()))
                                                        .param("firstName",FIRST_NAME)
                                                            .param("lastName",LAST_NAME))
               // then
               // 상태코드 - 4XX(=클라이언트 에러)
               // 뷰 - employeeLogin(=로그인 페이지)
               // 에러 메세지 - notFound.employee(=직원을 찾지 못했습니다 !)
               // 직원인증 - false(=로그인 실패)
               .andExpect(status().is4xxClientError())
               .andExpect(view().name("employeeLogin"))
               .andExpect(model().size(1))
               .andExpect(model().attribute("empLgnErr", ERROR_CODE))
               .andExpect(mvcResult -> assertFalse(manager.hasAuthentication(ID)));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @DisplayName("아이디를 미입력하고 로그인")
    @Test
    public void loginWithOutId()
    {
        // given
        final BigInteger ID = null;
        final String LAST_NAME = "Cole";
        final String FIRST_NAME = "Isabella";
        final String ERROR_CODE = "required.employee.employeeId";
        try
        {
            // when
            // 로그인
            mvc.perform(post("/employee/loginOut/login").param("employeeId", String.valueOf(ID))
                                                        .param("firstName", FIRST_NAME)
                            .param("lastName",LAST_NAME))
               // then
               // 상태코드 - 4XX(=클라이언트 에러)
               // 뷰 - employeeLogin(=로그인 페이지)
               // 에러 메세지 - required.employee.employeeId(=직원번호를 입력해주세요 !)
               // 직원인증 - false(=로그인 실패)
               .andExpect(status().is4xxClientError())
               .andExpect(view().name("employeeLogin"))
               .andExpect(model().size(1))
               .andExpect(model().attribute("empLgnErr", ERROR_CODE))
               .andExpect(mvcResult -> assertFalse(manager.hasAuthentication(ID)));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @DisplayName("아이디와 이름을 공백으로 입력하고 로그인")
    @Test
    public void loginWithBlankIdAndName()
    {
        // given
        final String ERROR_CODE = "required.employee.employeeIdName";
        try
        {
            // when
            // 로그인
            mvc.perform(post("/employee/loginOut/login").param("employeeId", "               ")
                                                        .param("name", "                     "))
               // then
               // 상태코드 - 4XX(=클라이언트 에러)
               // 뷰 - employeeLogin(=로그인 페이지)
               // 에러 메세지 - required.employee.employeeId(=직원번호와 직원이름을 입력해주세요 !)
               .andExpect(status().is4xxClientError())
               .andExpect(view().name("employeeLogin"))
                       .andExpect(model().size(1))
               .andExpect(model().attribute("empLgnErr", ERROR_CODE));


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
        final String ERROR_CODE = "required.employee.fullName";
        final BigInteger ID = BigInteger.valueOf(49);
        final String FIRST_NAME="         ";
        final String LAST_NAME ="         ";
        try
        {
            // when
            // 로그인
            mvc.perform(post("/employee/loginOut/login").param("employeeId", String.valueOf(ID))
                                                        .param("firstName", FIRST_NAME)
                            .param("lastName",LAST_NAME))
               // then
               // 상태코드 - 4XX(=클라이언트 에러)
               // 뷰 - employeeLogin(=로그인 페이지)
               // 에러 메세지 - required.employee.employeeId(=직원이름을 입력해주세요 !)
               .andExpect(status().is4xxClientError())
               .andExpect(view().name("employeeLogin"))
               .andExpect(model().size(1))
               .andExpect(model().attribute("empLgnErr", ERROR_CODE));


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
        final String ERROR_CODE = "required.employee.employeeId";
        final String LAST_NAME = "Cole";
        final String FIRST_NAME = "Isabella";
        try
        {
            // when
            // 로그인
            mvc.perform(post("/employee/loginOut/login").param("employeeId", "                   ")
                                                        .param("firstName", FIRST_NAME)
                                                        .param("lastName",LAST_NAME))
               // then
               // 상태코드 - 4XX(=클라이언트 에러)
               // 뷰 - employeeLogin(=로그인 페이지)
               // 에러 메세지 - required.employee.employeeId(=직원번호를 입력해주세요 !)
               .andExpect(status().is4xxClientError())
               .andExpect(view().name("employeeLogin"))
               .andExpect(model().size(1))
               .andExpect(model().attribute("empLgnErr", ERROR_CODE));


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
        final String ERROR_CODE = "required.employee.employeeId";
        final String LAST_NAME = "Cole";
        final String FIRST_NAME = "Isabella";
        try
        {
            // when
            // 로그인
            mvc.perform(post("/employee/loginOut/login").param("employeeId", "unknwon")
                                                        .param("firstName", FIRST_NAME)
                            .param("lastName",LAST_NAME))
               // then
               // 상태코드 - 4XX(=클라이언트 에러)
               // 뷰 - employeeLogin(=로그인 페이지)
               // 에러 메세지 - required.employee.employeeId(=직원 번호를 입력해주세요 !)
               .andExpect(status().is4xxClientError())
               .andExpect(view().name("employeeLogin"))
               .andExpect(model().size(1))
               .andExpect(model().attribute("empLgnErr", ERROR_CODE));


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
        final String FIRST_NAME = "1111";
        final String LAST_NAME = "1111";
        final String ERROR_CODE = "typeMismatch.employee.name";

        try
        {
            // when
            // 로그인
            mvc.perform(post("/employee/loginOut/login").param("employeeId", String.valueOf(ID))
                                                        .param("firstName", FIRST_NAME)
                            .param("lastName",LAST_NAME))
               // then
               // 상태코드 - 400(=클라이언트 에러)
               // 에러 메세지 - typeMismatch.employee.name(=직원이름을 문자로 입력해주세요 !)
               // 직원인증 - false(=로그인 실패)
               .andExpect(status().is4xxClientError())
               .andExpect(view().name("employeeLogin"))
               .andExpect(model().size(1))
               .andExpect(model().attribute("empLgnErr", ERROR_CODE))
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
        final String ERROR_CODE = "required.employee.fullName";
        final BigInteger ID = BigInteger.valueOf(49);
        final String FIRST_NAME = "";
        final String LAST_NAME = "";
        try
        {
            // when
            // 로그인
            mvc.perform(post("/employee/loginOut/login").param("employeeId", String.valueOf(ID))
                                                        .param("firstName", FIRST_NAME)
                            .param("lastName",LAST_NAME))
               // then
               // 상태코드 - 4XX(=클라이언트 에러)
               // 뷰 - employeeLogin(=로그인 페이지)
               // 에러 메세지 -required.employee.name(=직원이름을 입력해주세요 !)
               // 직원인증 - false(=로그인 실패)
               .andExpect(status().is4xxClientError())
               .andExpect(view().name("employeeLogin"))
               .andExpect(model().size(1))
               .andExpect(model().attribute("empLgnErr", ERROR_CODE))
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
        final String ERROR_CODE = "required.employee.employeeIdName";
        try
        {
            // when
            // 로그인
            mvc.perform(post("/employee/loginOut/login").param("employeeId", "")// 잘못 입력(에러 유도)
                                                        .param("name", "")) // 잘못 입력(에러 유도)
               // then
               // 상태코드 - 4XX(=클라이언트 에러)
               // 뷰 - employeeLogin(=로그인 페이지)
               // 에러 메세지 - required.employee.employeeIdName(=직원번호와 직원이름을 입력해주세요 !)
               .andExpect(status().is4xxClientError())
               .andExpect(view().name("employeeLogin"))
               .andExpect(model().size(1))
               .andExpect(model().attribute("empLgnErr", ERROR_CODE));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


}


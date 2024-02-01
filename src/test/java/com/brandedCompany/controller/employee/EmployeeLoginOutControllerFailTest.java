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
public class EmployeeLoginOutControllerFailTest
{
    private MockMvc mvc;
    @Autowired EmployeeLoginOutController employeeLoginOutController;
    @Autowired EmployeeAuthenticationManager manager;
    @Qualifier("CRUDServiceImpl") @Autowired CRUDService service;
    @Autowired MessageSourceAccessor messageSourceAccessor;
    @Autowired MessageConfig messageConfig;
    @Autowired private EmployeeLoginAuthenticationInterceptor authInterceptor;
    @Autowired EmployeeLoginValidator loginValidator;
    @Autowired EmployeeControllerGlobalCatcher catcher;

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

        final BigInteger ID = BigInteger.valueOf(49);
        if (manager.hasAuthentication(ID))
        {
            try
            {
                manager.removeAuthenticationById(ID);
            }
            catch (AuthenticationNullPointerException e)
            {
                e.printStackTrace();
            }
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


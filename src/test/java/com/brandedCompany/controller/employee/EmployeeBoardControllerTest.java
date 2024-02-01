package com.brandedCompany.controller.employee;

import com.brandedCompany.authentication.EmployeeAuthentication;
import com.brandedCompany.authentication.EmployeeAuthenticationManager;
import com.brandedCompany.authentication.exception.AuthenticationRuntimeException;
import com.brandedCompany.authentication.interceptor.EmployeeLoginAuthenticationInterceptor;
import com.brandedCompany.domain.Board;
import com.brandedCompany.domain.Employee;
import com.brandedCompany.serivce.OptionServiceImpl;
import com.brandedCompany.util.DomainUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.math.BigInteger;

import static com.brandedCompany.util.DomainUtils.TABLE.BOARDS;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/root-context.xml"})
@Sql({"classpath:schema/brandedCompany_truncate.sql", "classpath:schema/brandedCompany_data.sql"})
public class EmployeeBoardControllerTest
{

    @Autowired private EmployeeBoardController employeeBoardController;
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

        mvc = MockMvcBuilders.standaloneSetup(employeeBoardController)
                             .addInterceptors(employeeLoginAuthenticationInterceptor)
                             .setViewResolvers(internalResourceViewResolver)
                             .build();


        try
        {
            manager.addAuthentication(new EmployeeAuthentication(new Employee(EMPLOYEE_ID, FIRST_NAME, LAST_NAME)));
        }
        catch (AuthenticationRuntimeException e)
        {
            //      e.printStackTrace();
        }
    }

    @DisplayName("49번 직원이 새로운 게시글 페이지를 요청한다.")
    @Test
    public void goNewBoardPage()
    {
        try
        {
            //when
            mvc.perform(get("/employee/" + EMPLOYEE_ID + "/board/new"))
               //then
               .andExpect(status().isOk())
               .andExpect(view().name("employeeBoard"));
        }
        catch ( Exception e)
        {
            //    e.printStackTrace();
        }
    }

    @DisplayName("49번 직원이 511번 게시글을 작성한다.")
    @Test
    public void createBoard511ByEmployee49()
    {
        final BigInteger NEW_BOARD_ID=BigInteger.valueOf(511);
        final String NEW_BOARD_TITLE = "새로운 게시글을 써보겠습니다.";
        final String NEW_BOARD_CONTENT = "새로운 게시글의 내용입니다.";
        try
        {
            //49번 직원이 511번 게시글을 작성한다(=추가한다)
            //given
            String jsonData = new ObjectMapper().registerModule(new JavaTimeModule())
                                                .writeValueAsString(new Board(EMPLOYEE_ID, NAME, NEW_BOARD_TITLE, NEW_BOARD_CONTENT));
            //when
            mvc.perform(post("/employee/" + EMPLOYEE_ID + "/board")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonData))
               //then
               .andExpect(status().isOk());

        }
        catch (Exception e)
        {
            //    e.printStackTrace();
        }

        try
        {
            //49번 직원이 작성한 511번 게시글의 동일성을 검증한다.
            //given
            Board addedBoard = (Board) service.select(BOARDS, NEW_BOARD_ID);

            //then
            assertEquals(NEW_BOARD_TITLE,addedBoard.getTitle());
            assertEquals(NEW_BOARD_CONTENT,addedBoard.getContent());
            assertEquals(NEW_BOARD_ID,addedBoard.getBoardId());
        }catch (Exception e)
        {
            //        e.printStackTrace();
        }
    }

    @DisplayName("49번 직원이 511번 게시글을 작성하고 제목과 내용을 수정한다.")
    @Test
    public void updateBoard51ByEmployee49()
    {
        //given
        final BigInteger LAST_BOARD_ID=BigInteger.valueOf(511);
        final String NEW_TITLE = "제목 바꿉니다.";
        final String NEW_CONTENT = "내용 바꿉니다.";
        final Board BOARD = new Board(EMPLOYEE_ID, FIRST_NAME + " " + LAST_NAME, "내용", "제목");
        try
        {
            //49번 직원이 511번 게시글을 작성하고(=추가하고) 수정한다.
            //511번 게시글 추가
            assertTrue(service.insert(BOARD));

            //511번 게시글 수정본
            Board updatedBoard = new Board(LAST_BOARD_ID, EMPLOYEE_ID, NEW_CONTENT, NEW_TITLE);
            final String boardToJson = new ObjectMapper().registerModule(new JavaTimeModule())
                                                         .writeValueAsString(updatedBoard);
            //when
            mvc.perform(MockMvcRequestBuilders.patch("/employee/" + EMPLOYEE_ID + "/board/" + LAST_BOARD_ID)
                                              .content(boardToJson)
                                              .contentType(MediaType.APPLICATION_JSON))
               //then
               .andExpect(status().isOk());

        }
        catch (Exception e)
        {
            //       e.printStackTrace();
        }

        try
        {
            //49번 직원이 수정한 게시글의 동일성을 검증한다.
            //given
            Board boardUpdated = (Board) service.select(BOARDS, LAST_BOARD_ID);
            //then
            assertEquals(LAST_BOARD_ID,boardUpdated.getBoardId());
            assertEquals(NEW_TITLE,boardUpdated.getTitle());
            assertEquals(NEW_CONTENT,boardUpdated.getContent());
        }catch (Exception e)
        {
            //    e.printStackTrace();
        }
    }

    @DisplayName("49번 직원이 511번 게시글을 삭제한다.")
    @Test
    public void deleteBoard511ByEmployee49()
    {
        //given
        final BigInteger NEW_BOARD_ID = BigInteger.valueOf(511);
        final String TITLE = "제목";
        final String CONTENT = "내용";
        try
        {
            //49번 직원이 511번 게시글을 작성하고(=추가하고) 삭제한다.
            assertTrue(service.insert(new Board(EMPLOYEE_ID, NAME, TITLE, CONTENT)));

            //when
            //테스트 타겟 삭제
            mvc.perform(delete("/employee/" + EMPLOYEE_ID + "/board/" + NEW_BOARD_ID))
               //then
               .andExpect(status().isOk());
        }
        catch (Exception e)
        {
            //      e.printStackTrace();
        }

        try
        {
            //49번 직원이 삭제한 게시글을 유효성 검증한다.
            //then
            assertFalse(service.isExist(BOARDS, NEW_BOARD_ID));
        }
        catch (Exception e)
        {
            //         e.printStackTrace();
        }
    }

}
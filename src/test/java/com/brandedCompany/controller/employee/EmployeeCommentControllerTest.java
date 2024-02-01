package com.brandedCompany.controller.employee;

import com.brandedCompany.authentication.EmployeeAuthentication;
import com.brandedCompany.authentication.EmployeeAuthenticationManager;
import com.brandedCompany.authentication.exception.AuthenticationRuntimeException;
import com.brandedCompany.authentication.interceptor.EmployeeLoginAuthenticationInterceptor;
import com.brandedCompany.domain.Board;
import com.brandedCompany.domain.Comment;
import com.brandedCompany.domain.Employee;
import com.brandedCompany.serivce.OptionServiceImpl;
import com.brandedCompany.util.DomainUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.assertj.core.api.AssertFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.internal.verification.MockAwareVerificationMode;
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

import javax.print.attribute.standard.MediaSize;
import java.math.BigInteger;
import java.util.Objects;

import static com.brandedCompany.util.DomainUtils.TABLE.COMMENTS;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/root-context.xml"})
@Sql({"classpath:schema/brandedCompany_truncate.sql", "classpath:schema/brandedCompany_data.sql"})
public class EmployeeCommentControllerTest
{

    @Autowired private EmployeeCommentController controller;

    @Autowired private EmployeeLoginAuthenticationInterceptor interceptor;
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
                             .addInterceptors(interceptor)
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


    @DisplayName("49번 직원이 510번 게시글에 96번 댓글(=새로운 댓글)을 작성한다.")
    @Test
    public void createCommentOnBoard510ByEmployee49()
    {
        //given
        final BigInteger NEW_BOARD_ID = BigInteger.valueOf(510);
        final Comment comment = new Comment(NEW_BOARD_ID, EMPLOYEE_ID, "댓글 처음 남겨봅니다.", NAME);
        try
        {
            //49번 직원이 510번 게시글에 96번 댓글(=새로운 댓글)을 작성한다.
            final String commentToJson = new ObjectMapper().registerModule(new JavaTimeModule())
                                                           .writeValueAsString(comment);
            //when
            mvc.perform(post("/employee/" + EMPLOYEE_ID + "/board/" + NEW_BOARD_ID + "/comment").contentType(MediaType.APPLICATION_JSON)
                                                                                                .content(commentToJson))
               //then
               .andExpect(status().isOk());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            //49번 직원이 510번 게시글에 작성한 댓글의 유효성을 검증한다.
            Assertions.assertTrue(service.isExistComment(NEW_BOARD_ID));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @DisplayName("49번 직원이 510번 게시글에 있는 96번 댓글에 답글을 작성한다.")
    @Test
    public void createReplyOnComment97InBoard510ByEmployee49()
    {
        //게시글 번호
        final BigInteger BOARD_ID = BigInteger.valueOf(510);
        //댓글번호(=부모댓글)
        final BigInteger PARENT_COMMENT_ID = BigInteger.valueOf(96);
        //답글번호(=자식댓글)
        final BigInteger CHILD_COMMENT_ID = PARENT_COMMENT_ID.add(BigInteger.ONE);
        //댓글내용
        final String COMMENT_CONTENT = "댓글1";
        //답글내용
        final String REPLY_CONTENT = "댓글1의 답글1";
        try
        {
            //49번 직원이 510번 게시글에 96번 댓글에 답글을 작성한다.
            //given
            //댓글
            Comment comment = new Comment(BOARD_ID, EMPLOYEE_ID, COMMENT_CONTENT, NAME);
            //96번 댓글 추가
            assertTrue(service.insert(comment));
            //답글
            Comment replyComment = new Comment(BOARD_ID, EMPLOYEE_ID, REPLY_CONTENT, NAME);

            String replyToJson = new ObjectMapper().registerModule(new JavaTimeModule())
                                                   .writeValueAsString(replyComment);
            //when
            mvc.perform(MockMvcRequestBuilders.post("/employee/" + EMPLOYEE_ID + "/board/" + BOARD_ID + "/parentComment/" + PARENT_COMMENT_ID + "/comment/")
                                              .content(replyToJson)
                                              .contentType(MediaType.APPLICATION_JSON))
               //then
               .andExpect(status().isOk());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            //96번 댓글과 답글의 유효성을 검증한다.
            assertTrue(service.isExist(COMMENTS, PARENT_COMMENT_ID));
            assertTrue(service.isExist(COMMENTS, CHILD_COMMENT_ID));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @DisplayName("49번 직원이 510번 게시글의 96번 답글을 삭제한다.")
    @Test
    public void deleteComment95OnBoard510ByEmployee49()
    {
        //given
        final BigInteger BOARD_ID = BigInteger.valueOf(510);
        final BigInteger NEW_COMMENT_ID = BigInteger.valueOf(96);
        final String NEW_COMMENT_CONTENT = "댓글";
        try
        {
            //49번 직원이 510번 게시글의 96번 댓글을 추가하고 삭제한다.
            assertTrue(service.insert(new Comment(BOARD_ID, EMPLOYEE_ID, NEW_COMMENT_CONTENT, NAME)));
            //when
            mvc.perform(delete("/employee/" + EMPLOYEE_ID + "/board/" + BOARD_ID + "/parentComment/" + NEW_COMMENT_ID + "/comment/" + NEW_COMMENT_ID))
               //the
               .andExpect(status().isOk());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        try
        {
            assertFalse(service.isExist(COMMENTS, NEW_COMMENT_ID));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


}
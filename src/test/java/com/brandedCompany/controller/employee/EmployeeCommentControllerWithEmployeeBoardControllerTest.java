package com.brandedCompany.controller.employee;

import com.brandedCompany.authentication.EmployeeAuthentication;
import com.brandedCompany.authentication.EmployeeAuthenticationManager;
import com.brandedCompany.authentication.exception.AuthenticationRuntimeException;
import com.brandedCompany.authentication.interceptor.EmployeeLoginAuthenticationInterceptor;
import com.brandedCompany.domain.Comment;
import com.brandedCompany.domain.Employee;
import com.brandedCompany.serivce.OptionServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.sql.DataSource;
import java.math.BigInteger;

import static com.brandedCompany.util.DomainUtils.TABLE.COMMENTS;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/root-context.xml"})
@Sql({"classpath:schema/brandedCompany_truncate.sql", "classpath:schema/brandedCompany_data.sql"})
public class EmployeeCommentControllerWithEmployeeBoardControllerTest
{

    @Autowired private EmployeeBoardController boardCon;
    @Autowired private EmployeeCommentController commentCon;
    @Autowired private EmployeeLoginAuthenticationInterceptor interceptor;
    @Autowired private EmployeeAuthenticationManager manager;
    @Autowired private DataSource dataSource;
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

        mvc = MockMvcBuilders.standaloneSetup(commentCon)
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


    public void setUp(Object controller)
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

    @DisplayName("49번 직원이 작성한 댓글은 해당 게시글이 삭제되면서 같이 삭제된다.")
    @Test
    public void deleteCascadingBoard49_comment96()
    {
        setUp(boardCon);

        //given
        final BigInteger BOARD_ID = BigInteger.valueOf(510); //게시글 번호
        final BigInteger BOARD_WRITER_EMPLOYEE_ID = BigInteger.valueOf(17); //게시글 작성자 번호
        final String BOARD_WRITER = "Frederick Price";//게시글 작성자 이름
        final String NEW_COMMENT_CONTENT = "댓글1";//게시글 댓글
        final BigInteger COMMENT_ID = BigInteger.valueOf(96);//부모댓글 번호

        try
        {
            //510번 게시글에 댓글추가
            assertTrue(service.insert(new Comment(BOARD_ID, COMMENT_ID, EMPLOYEE_ID, NEW_COMMENT_CONTENT, NAME)));

            //when
            mvc.perform(delete("/employee/" + BOARD_WRITER_EMPLOYEE_ID + "/board/" + BOARD_ID))
               //then
               .andExpect(status().isOk());

            //댓글 삭제 유효성 검사
            Assertions.assertFalse(service.isExist(COMMENTS, COMMENT_ID));
        }
        catch (Exception e)
        {

            e.printStackTrace();
        }
    }

    @DisplayName("49번 직원이 작성한 부모댓글과 자식댓글은 해당 게시글이 삭제되면서 같이 삭제된다.")
    @Test
    public void deleteCascadingBoard49_comment96AndComment97()
    {
        setUp(boardCon);

        //given
        final BigInteger BOARD_ID = BigInteger.valueOf(510); //게시글 번호
        final BigInteger BOARD_WRITER_EMPLOYEE_ID = BigInteger.valueOf(17); //게시글 작성자 번호
        final String BOARD_WRITER = "Frederick Price";//게시글 작성자 이름
        final String COMMENT_CONTENT = "댓글1";//게시글 댓글
        final BigInteger PARENT_COMMENT_ID = BigInteger.valueOf(96);//부모댓글 번호
        final BigInteger CHILD_COMMENT_ID = BigInteger.valueOf(97);//자식댓글 번호
        final String CHILD_COMMENT_CONTENT = "댓글1의 답글1";//자식댓글 내용(답글)

        try
        {
            //510번 게시글에 부모 댓글 추가
            assertTrue(service.insert(new Comment(BOARD_ID, EMPLOYEE_ID, COMMENT_CONTENT, NAME)));
            //510번 게시글의 자식 댓글 추가
            assertTrue(service.insert(new Comment(BOARD_ID, PARENT_COMMENT_ID, EMPLOYEE_ID, CHILD_COMMENT_CONTENT, NAME)));

            //when
            mvc.perform(delete("/employee/" + BOARD_WRITER_EMPLOYEE_ID + "/board/" + BOARD_ID))
               //then
               .andExpect(status().isOk());

            //부모댓글 삭제 유효성 검사
            Assertions.assertFalse(service.isExist(COMMENTS, PARENT_COMMENT_ID));
            //자식댓글 삭제 유효성 검사
            Assertions.assertFalse(service.isExist(COMMENTS, CHILD_COMMENT_ID));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}
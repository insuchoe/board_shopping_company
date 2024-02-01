package com.brandedCompany.serivce;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/root-context.xml"})
@Sql({"classpath:schema/data-finalize/brandedCompany_employees_finalize_data.sql", "classpath:schema/data-finalize/brandedCompany_employees_finalize_data.sql"})
@Sql({"classpath:schema/data-initialize/brandedCompany_customers_initialize_data.sql"})
@DisplayName("EMPLOYEE TABLE")
public class EmployeeServiceImplTest {

    @Test
    public void 컬럼_총_개수() {
    }

    @Test
    public void 모든_컬럼_삭제() {
    }

    @Test
    public void 컬럼_삭제() {
    }

    @Test
    public void 식별번호로_컬럼_찾기() {
    }

    @Test
    public void 모든_컬럼_얻기() {
    }


    @Test
    public void findId() {
    }

    @Test
    public void findName() {
    }

    @Test
    public void findFirstName() {
    }

    @Test
    public void findLastName() {
    }


    @Test
    public void 컬럼_변경() {
    }

    @Test
    public void addMany() {
    }

    @Test
    public void addOne() {
    }

    @Test
    public void findNoDigitName() {
    }

    @Test
    public void findDigitName() {
    }
}
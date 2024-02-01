package com.brandedCompany.repository;

import com.brandedCompany.domain.*;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;

import static com.brandedCompany.util.DomainUtils.NameLocation.*;
import static com.brandedCompany.util.DomainUtils.TABLE.*;
import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/root-context.xml"})
@Sql({"classpath:schema/brandedCompany_truncate.sql",
        "classpath:schema/brandedCompany_data.sql"})
public class OptionRepositoryImplTest {
    @Autowired
    OptionRepository optionRepository;

    @DisplayName("OptionRepository#selectByName test")
    @Test
    public void selectByEmployeeName() {
        try {
//            EMPLOYEE 이름 + 성
            final String empFirstName = "Tommy";
            Collection<? extends Domain> tommy = optionRepository.selectByName(EMPLOYEES, empFirstName, FIRST);
            Iterator<? extends Domain> iter = tommy.iterator();

            Assertions.assertEquals(1, tommy.size());
            Employee emp = (Employee) iter.next();

            assertEquals(empFirstName, emp.getFirstName());

//            EMPLOYEE 이름
            final String empLastName = "Bailey";
            tommy = optionRepository.selectByName(EMPLOYEES, empLastName, LAST);
            assertEquals(1, tommy.size());
            emp = (Employee) tommy.iterator().next();
            assertEquals(empLastName, emp.getLastName());

//          EMPLOYEE 성
            final String empName = "Tommy Bailey";
            tommy = optionRepository.selectByName(EMPLOYEES, empName, DEFAULT);
            assertEquals(1, tommy.size());
            emp = (Employee) tommy.iterator().next();
            assertEquals(empName, emp.getFirstName() + " " + emp.getLastName());
        } catch (Exception e) {
            //         e.printStackTrace();
        }


    }

    @DisplayName("OptionRepository#selectByName test")
    @Test
    public void selectByBoardPublisher() {
        try {
//            BOARDS 이름
            final String PUBLISHER = "Delphine Benford";
            Collection<? extends Domain> domains = optionRepository.selectByName(BOARDS, PUBLISHER, DEFAULT);
            Iterator<? extends Domain> iter = domains.iterator();
            while (iter.hasNext()) {
                Board board = (Board) iter.next();
                assertEquals(PUBLISHER, board.getPublisher());
            }

//          BOARDS 성
            final String FIRST_NAME = PUBLISHER.split(" ")[0];
            domains = optionRepository.selectByName(BOARDS, FIRST_NAME, FIRST);
            iter = domains.iterator();
            while (iter.hasNext()) {
                Board board = (Board) iter.next();
                assertEquals(FIRST_NAME, board.getPublisher().split(" ")[0]);
            }

//          BOARDS 이름 + 성
            final String LAST_NAME = PUBLISHER.split(" ")[1];
            domains = optionRepository.selectByName(BOARDS, LAST_NAME, LAST);
            iter = domains.iterator();
            while (iter.hasNext()) {
                Board board = (Board) iter.next();
                assertEquals(LAST_NAME, board.getPublisher().split(" ")[1]);
            }


        } catch (Exception e) {
            //       e.printStackTrace();
        }

    }


    @DisplayName("OptionRepository#selectByName test")
    @Test
    public void selectByCustomerName() {
        final String NAME = "Saxon Dennitts";
        try {
//            CUSTOMER 이름+ 성
            Collection<? extends Domain> domains = optionRepository.selectByName(CUSTOMERS, NAME, DEFAULT);
            Iterator<? extends Domain> iter = domains.iterator();
            while (iter.hasNext()) {
                Customer customer = (Customer) iter.next();
                assertEquals(NAME, customer.getName());
            }

//            CUSTOMER 이름
            final String FIRST_NAME = NAME.split(" ")[0];
            domains = optionRepository.selectByName(CUSTOMERS, FIRST_NAME, FIRST);
            iter = domains.iterator();
            while (iter.hasNext()) {
                Customer customer = (Customer) iter.next();
                assertEquals(FIRST_NAME, customer.getName().split(" ")[0]);
            }

//            CUSTOMER 성
            final String LAST_NAME = NAME.split(" ")[1];
            domains = optionRepository.selectByName(CUSTOMERS, LAST_NAME, LAST);
            iter = domains.iterator();
            while (iter.hasNext()) {
                Customer customer = (Customer) iter.next();
                assertEquals(LAST_NAME, customer.getName().split(" ")[1]);
            }
        } catch (Exception e) {
            //        e.printStackTrace();
        }
    }

    @DisplayName("OptionRepository#selectByName test")
    @Test
    public void selectByCommentPublisher() {
        try {
//            COMMENT.PUBLISHER 이름+ 성
            final String NAME = "Delphine Benford";
            Collection<? extends Domain> domains = optionRepository.selectByName(COMMENTS, NAME, DEFAULT);
            Iterator<? extends Domain> iter = domains.iterator();
            while (iter.hasNext()) {
                Comment comment = (Comment) iter.next();
                assertEquals(NAME, comment.getPublisher());
            }


//            COMMENT.PUBLISHER 이름
            final String FIRST_NAME = "Delphine";
            domains = optionRepository.selectByName(COMMENTS, FIRST_NAME, FIRST);
            iter = domains.iterator();
            while (iter.hasNext()) {
                Comment comment = (Comment) iter.next();
                assertEquals(FIRST_NAME, comment.getPublisher().split(" ")[0]);
            }

//            COMMENT.PUBLISHER 성
            final String LAST_NAME = "Benford";
            domains = optionRepository.selectByName(COMMENTS, LAST_NAME, LAST);
            iter = domains.iterator();
            while (iter.hasNext()) {
                Comment comment = (Comment) iter.next();
                assertEquals(LAST_NAME, comment.getPublisher().split(" ")[1]);
            }

        } catch (Exception e) {
            //         e.printStackTrace();
        }
    }

    @DisplayName("OptionRepository#selectBoardByEmpId test")
    @Test
    public void selectByEmpId() {

        final BigInteger EMP_ID = BigInteger.valueOf(168);

        try {
//            EMPLOYEE_ID 로 BOARD 취득
            Employee emp = (Employee) optionRepository.select(EMPLOYEES, EMP_ID);
            assertNotNull(emp);

            Collection<Board> boards = optionRepository.selectBoardByEmpId(EMP_ID);

            for (Board board : boards) assertEquals(emp.getEmployeeId(), board.getEmployeeId());
        } catch (Exception e) {
            //      e.printStackTrace();
        }
    }



















}
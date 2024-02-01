package com.brandedCompany.controller.employee;

import com.brandedCompany.authentication.EmployeeAuthentication;
import com.brandedCompany.authentication.EmployeeAuthenticationManager;
import com.brandedCompany.authentication.exception.AuthenticationRuntimeException;
import com.brandedCompany.authentication.interceptor.EmployeeLoginAuthenticationInterceptor;
import com.brandedCompany.domain.Employee;
import com.brandedCompany.domain.OrderHistory;
import com.brandedCompany.domain.handler.OrderHistoryPageHandler;
import com.brandedCompany.domain.searchCondition.OrderHistorySearchCondition;
import com.brandedCompany.serivce.PagingAndSortingService;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.math.BigInteger;
import java.util.Collection;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/root-context.xml"})
@Sql({"classpath:schema/brandedCompany_truncate.sql", "classpath:schema/brandedCompany_data.sql"})
public class AllCustomerOrderHistoryControllerPagingTest
{
    private MockMvc mvc;
    @Autowired AllCustomerOrderHistoryController controller;
    @Autowired EmployeeAuthenticationManager manager;
    @Qualifier("pagingAndSortingServiceImpl") @Autowired PagingAndSortingService service;
    @Autowired private EmployeeLoginAuthenticationInterceptor authInterceptor;

    final BigInteger EMPLOYEE_ID = BigInteger.valueOf(49);
    final String FIRST_NAME = "Isabella";
    final String LAST_NAME = "Cole";

    @Before
    public void setUp()
    {
        InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();

        internalResourceViewResolver.setPrefix("/WEB-INF/views/");
        internalResourceViewResolver.setSuffix(".jsp");
        mvc = MockMvcBuilders.standaloneSetup(controller)
                             .setViewResolvers(internalResourceViewResolver)
                             .addInterceptors(authInterceptor)
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

    @DisplayName("직원49번이 1페이지를 요청할 때 페이지 핸들러를 검증한다.")
    @Test
    public void validatePageHandlerBySearched_page1()
    {
        try
        {
            //given
            //검색 페이지 : 1
            final int PAGE=1;
            //검색조건
            OrderHistorySearchCondition searchCondition = new OrderHistorySearchCondition(PAGE);
            //검색조건에 부합되는 주문내역 수
            BigInteger count = service.count(searchCondition);
            //검색조건에 부합되는 페이지 핸들러
            OrderHistoryPageHandler pageHandler = new OrderHistoryPageHandler(count, searchCondition);

            //when
            MvcResult mvcResult = mvc.perform(get("/employee/" + EMPLOYEE_ID + "/orderHistory?page=1"))
                                     //then
                                     .andExpect(status().isOk())
                                     .andExpect(view().name("allCustomerOrderHistory"))
                                     .andExpect(model().attributeExists("orderHistoryPageHandler"))
                                     .andReturn();

            OrderHistoryPageHandler pageHandlerFromModel = (OrderHistoryPageHandler) mvcResult.getModelAndView()
                                                                                              .getModel()
                                                                                              .get("orderHistoryPageHandler");
            //해당 url 에서 부여받은 '페이지 핸들러'와 동일한지 검사
            //then
            assertEquals(pageHandler, pageHandlerFromModel);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @DisplayName("49번직원이 11페이지를 요청할 때 페이지 핸들러를 검증한다.")
    @Test
    public void validatePageHandlerBySearched_page11()
    {
        try
        {
            //given
            //검색 페이지 : 11
            final int PAGE = 11;
            //검색조건
            OrderHistorySearchCondition searchCondition = new OrderHistorySearchCondition(PAGE);
            //검색조건에 부합되는 주문내역 수
            BigInteger count = service.count(searchCondition);
            //검색조건에 부합되는 페이지 핸들러
            OrderHistoryPageHandler pageHandler = new OrderHistoryPageHandler(count, searchCondition);

            //when
            MvcResult mvcResult = mvc.perform(get("/employee/" + EMPLOYEE_ID + "/orderHistory?page=11"))
                                     //then
                                     .andExpect(status().isOk())
                                     .andExpect(view().name("allCustomerOrderHistory"))
                                     .andExpect(model().attributeExists("orderHistoryPageHandler"))
                                     .andReturn();

            OrderHistoryPageHandler pageHandlerFromModel = (OrderHistoryPageHandler) mvcResult.getModelAndView()
                                                                                              .getModel()
                                                                                              .get("orderHistoryPageHandler");
            //해당 url 에서 부여받은 '페이지 핸들러'와 동일한지 검사
            //then
            assertEquals(pageHandler, pageHandlerFromModel);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @DisplayName("49번직원이 주문날짜,오름차순,1페이지를 요청할 때 주문내역 아이템을 검증한다.")
    @Test
    public void validatePageHandlerBySearched_orderDate_ASC_page1()
    {
        try
        {
            //given
            final Integer PAGE = 1;
            final OrderHistorySearchCondition.OrderHistorySortTarget TARGET = OrderHistorySearchCondition.OrderHistorySortTarget.ORDER_DATE;
            final OrderHistorySearchCondition.OrderHistorySortDirection DIRECTION = OrderHistorySearchCondition.OrderHistorySortDirection.ASC;
            OrderHistorySearchCondition.OrderHistorySearchConditionBuilder builder = new OrderHistorySearchCondition.OrderHistorySearchConditionBuilder();
            //검색 페이지 : 1
            builder.page(PAGE);
            //정렬대상 : 주문날짜
            builder.target(TARGET);
            //정렬방향 : 오름차순
            builder.direction(DIRECTION);
            //검색조건
            OrderHistorySearchCondition searchCondition = builder.build();
            //검색조건에 부합되는 주문내역 데이터(=데이터베이스에서 전달받은 값)
            Collection<OrderHistory> allOrderHistory = (Collection<OrderHistory>) service.pageAndSort(searchCondition);

            //when
            MvcResult mvcResult = mvc.perform(get("/employee/" + EMPLOYEE_ID + "/orderHistory?page=1&target=ORDER_DATE&direction=ASC"))
                                     //then
                                     .andExpect(status().isOk())
                                     .andExpect(view().name("allCustomerOrderHistory"))
                                     .andExpect(model().attributeExists("orderHistoryPageHandler"))
                                     .andReturn();
            //url 요청에서 전달받은 값
            Collection<OrderHistory> allOrderHistoryItem = (Collection<OrderHistory>) mvcResult.getModelAndView()
                                                                                               .getModel()
                                                                                               .get("orderHistory");

            Assertions.assertEquals(allOrderHistory.size(), allOrderHistoryItem.size());

            Object[] itemToArray = allOrderHistory.toArray();
            Object[] otherItemToArray = allOrderHistoryItem.toArray();
            boolean allMatch = IntStream.range(0, itemToArray.length)
                                        .allMatch(idx -> itemToArray[idx].equals(otherItemToArray[idx]));
            assertTrue(allMatch);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @DisplayName("49번직원이 주문날짜,오름차순,11페이지를 요청할 때 주문내역 아이템을 검증한다.")
    @Test
    public void validatePageHandlerBySearched_orderDate_ASC_page11()
    {
        try
        {
            //given
            final Integer PAGE = 11;
            final OrderHistorySearchCondition.OrderHistorySortTarget TARGET = OrderHistorySearchCondition.OrderHistorySortTarget.ORDER_DATE;
            final OrderHistorySearchCondition.OrderHistorySearchConditionBuilder builder = new OrderHistorySearchCondition.OrderHistorySearchConditionBuilder();
            final OrderHistorySearchCondition.OrderHistorySortDirection DIRECTION = OrderHistorySearchCondition.OrderHistorySortDirection.ASC;
            //검색 페이지 : 11
            builder.page(PAGE);
            //정렬대상 : 주문날짜
            builder.target(TARGET);
            //정렬방향 : 오름차순
            builder.direction(DIRECTION);
            //검색조건
            OrderHistorySearchCondition searchCondition = builder.build();
            //검색조건에 부합되는 주문내역 데이터(=데이터베이스에서 전달받은 값)
            Collection<OrderHistory> allOrderHistory = (Collection<OrderHistory>) service.pageAndSort(searchCondition);

            //when
            MvcResult mvcResult = mvc.perform(get("/employee/" + EMPLOYEE_ID + "/orderHistory?page=11&target=ORDER_DATE&direction=ASC"))
                                     //then
                                     .andExpect(status().isOk())
                                     .andExpect(view().name("allCustomerOrderHistory"))
                                     .andExpect(model().attributeExists("orderHistoryPageHandler"))
                                     .andReturn();

            //url 요청에서 전달받은 값
            Collection<OrderHistory> allOrderHistoryItem = (Collection<OrderHistory>) mvcResult.getModelAndView()
                                                                                               .getModel()
                                                                                               .get("orderHistory");

            //데이터베이스에서 전달받은 주문내역과
            //url 요청에서 전달받은 주문내역이 같은지 검증
            Assertions.assertEquals(allOrderHistory.size(), allOrderHistoryItem.size());

            Object[] itemToArray = allOrderHistory.toArray();
            Object[] otherItemToArray = allOrderHistoryItem.toArray();
            boolean allMatch = IntStream.range(0, itemToArray.length)
                                        .allMatch(idx -> itemToArray[idx].equals(otherItemToArray[idx]));

            assertTrue(allMatch);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @DisplayName("49번직원이 주문날짜,내림차순,1페이지를 요청할 때 주문내역 아이템을 검증한다.")
    @Test
    public void validatePageHandlerBySearched_orderDate_DESC_page1()
    {
        try
        {
            //given
            final Integer PAGE = 1;
            final OrderHistorySearchCondition.OrderHistorySortTarget TARGET = OrderHistorySearchCondition.OrderHistorySortTarget.ORDER_DATE;
            final OrderHistorySearchCondition.OrderHistorySearchConditionBuilder builder = new OrderHistorySearchCondition.OrderHistorySearchConditionBuilder();
            final OrderHistorySearchCondition.OrderHistorySortDirection DIRECTION = OrderHistorySearchCondition.OrderHistorySortDirection.DESC;
            //검색 페이지 : 1
            builder.page(PAGE);
            //정렬대상 : 주문날짜
            builder.target(TARGET);
            //정렬방향 : 내림차순
            builder.direction(DIRECTION);
            //검색조건
            OrderHistorySearchCondition searchCondition = builder.build();
            //검색조건에 부합되는 주문내역 데이터(=데이터베이스에서 전달받은 값)
            Collection<OrderHistory> allOrderHistory = (Collection<OrderHistory>) service.pageAndSort(searchCondition);

            //when
            MvcResult mvcResult = mvc.perform(get("/employee/" + EMPLOYEE_ID + "/orderHistory?page=1&target=ORDER_DATE&direction=DESC"))
                                     //then
                                     .andExpect(status().isOk())
                                     .andExpect(view().name("allCustomerOrderHistory"))
                                     .andExpect(model().attributeExists("orderHistoryPageHandler"))
                                     .andReturn();

            //url 요청에서 전달받은 값
            Collection<OrderHistory> allOrderHistoryItem = (Collection<OrderHistory>) mvcResult.getModelAndView()
                                                                                               .getModel()
                                                                                               .get("orderHistory");

            //데이터베이스에서 전달받은 주문내역과
            //url 요청에서 전달받은 주문내역이 같은지 검증
            Assertions.assertEquals(allOrderHistory.size(), allOrderHistoryItem.size());

            Object[] itemToArray = allOrderHistory.toArray();
            Object[] otherItemToArray = allOrderHistoryItem.toArray();
            boolean allMatch = IntStream.range(0, itemToArray.length)
                                        .allMatch(idx -> itemToArray[idx].equals(otherItemToArray[idx]));

            assertTrue(allMatch);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @DisplayName("49번직원이 주문날짜,내림차순,11페이지를 요청할 때 주문내역 아이템을 검증한다.")
    @Test
    public void validatePageHandlerBySearched_orderDate_DESC_page11()
    {
        try
        {
            //given
            final Integer PAGE = 11;
            final OrderHistorySearchCondition.OrderHistorySortTarget TARGET = OrderHistorySearchCondition.OrderHistorySortTarget.ORDER_DATE;
            final OrderHistorySearchCondition.OrderHistorySearchConditionBuilder builder = new OrderHistorySearchCondition.OrderHistorySearchConditionBuilder();
            final OrderHistorySearchCondition.OrderHistorySortDirection DIRECTION = OrderHistorySearchCondition.OrderHistorySortDirection.DESC;
            //검색 페이지 : 11
            builder.page(PAGE);
            //정렬대상 : 주문날짜
            builder.target(TARGET);
            //정렬방향 : 내림차순
            builder.direction(DIRECTION);
            //검색조건
            OrderHistorySearchCondition searchCondition = builder.build();
            //검색조건에 부합되는 주문내역 데이터(=데이터베이스에서 전달받은 값)
            Collection<OrderHistory> allOrderHistory = (Collection<OrderHistory>) service.pageAndSort(searchCondition);

            //when
            MvcResult mvcResult = mvc.perform(get("/employee/" + EMPLOYEE_ID + "/orderHistory?page=11&target=ORDER_DATE&direction=DESC"))
                                     //then
                                     .andExpect(status().isOk())
                                     .andExpect(view().name("allCustomerOrderHistory"))
                                     .andExpect(model().attributeExists("orderHistoryPageHandler"))
                                     .andReturn();

            //url 요청에서 전달받은 값
            Collection<OrderHistory> allOrderHistoryItem = (Collection<OrderHistory>) mvcResult.getModelAndView()
                                                                                               .getModel()
                                                                                               .get("orderHistory");

            //데이터베이스에서 전달받은 주문내역과
            //url 요청에서 전달받은 주문내역이 같은지 검증
            Assertions.assertEquals(allOrderHistory.size(), allOrderHistoryItem.size());

            Object[] itemToArray = allOrderHistory.toArray();
            Object[] otherItemToArray = allOrderHistoryItem.toArray();
            boolean allMatch = IntStream.range(0, itemToArray.length)
                                        .allMatch(idx -> itemToArray[idx].equals(otherItemToArray[idx]));

            assertTrue(allMatch);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @DisplayName("49번직원이 주문상태,오름차순,1페이지를 요청할 때 주문내역 아이템을 검증한다.")
    @Test
    public void validatePageHandlerBySearched_orderStatus_ASC_page1()
    {
        try
        {
            //given
            final Integer PAGE = 1;
            final OrderHistorySearchCondition.OrderHistorySortTarget TARGET = OrderHistorySearchCondition.OrderHistorySortTarget.ORDER_STATUS;
            final OrderHistorySearchCondition.OrderHistorySearchConditionBuilder builder = new OrderHistorySearchCondition.OrderHistorySearchConditionBuilder();
            final OrderHistorySearchCondition.OrderHistorySortDirection DIRECTION = OrderHistorySearchCondition.OrderHistorySortDirection.ASC;
            //검색 페이지 : 1
            builder.page(PAGE);
            //정렬대상 : 주문상태
            builder.target(TARGET);
            //정렬방향 : 오름차순
            builder.direction(DIRECTION);
            //검색조건
            OrderHistorySearchCondition searchCondition = builder.build();
            //검색조건에 부합되는 주문내역 데이터(=데이터베이스에서 전달받은 값)
            Collection<OrderHistory> allOrderHistory = (Collection<OrderHistory>) service.pageAndSort(searchCondition);

            //when
            MvcResult mvcResult = mvc.perform(get("/employee/" + EMPLOYEE_ID + "/orderHistory?page=1&target=ORDER_STATUS&direction=ASC"))
                                     //then
                                     .andExpect(status().isOk())
                                     .andExpect(view().name("allCustomerOrderHistory"))
                                     .andExpect(model().attributeExists("orderHistoryPageHandler"))
                                     .andReturn();

            //url 요청에서 전달받은 값
            Collection<OrderHistory> allOrderHistoryItem = (Collection<OrderHistory>) mvcResult.getModelAndView()
                                                                                               .getModel()
                                                                                               .get("orderHistory");

            //데이터베이스에서 전달받은 주문내역과
            //url 요청에서 전달받은 주문내역이 같은지 검증
            Assertions.assertEquals(allOrderHistory.size(), allOrderHistoryItem.size());

            Object[] itemToArray = allOrderHistory.toArray();
            Object[] otherItemToArray = allOrderHistoryItem.toArray();
            boolean allMatch = IntStream.range(0, itemToArray.length)
                                        .allMatch(idx -> itemToArray[idx].equals(otherItemToArray[idx]));

            assertTrue(allMatch);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @DisplayName("49번직원이 주문상태,오름차순,11페이지를 요청할 때 주문내역 아이템을 검증한다.")
    @Test
    public void validatePageHandlerBySearched_orderStatus_ASC_page11()
    {
        try
        {
            //given
            final Integer PAGE = 11;
            final OrderHistorySearchCondition.OrderHistorySortTarget TARGET = OrderHistorySearchCondition.OrderHistorySortTarget.ORDER_STATUS;
            final OrderHistorySearchCondition.OrderHistorySearchConditionBuilder builder = new OrderHistorySearchCondition.OrderHistorySearchConditionBuilder();
            final OrderHistorySearchCondition.OrderHistorySortDirection DIRECTION = OrderHistorySearchCondition.OrderHistorySortDirection.ASC;
            //검색 페이지 : 11
            builder.page(PAGE);
            //정렬대상 : 주문상태
            builder.target(TARGET);
            //정렬방향 : 오름차순
            builder.direction(DIRECTION);
            //검색조건
            OrderHistorySearchCondition searchCondition = builder.build();
            //검색조건에 부합되는 주문내역 데이터(=데이터베이스에서 전달받은 값)
            Collection<OrderHistory> allOrderHistory = (Collection<OrderHistory>) service.pageAndSort(searchCondition);

            //when
            MvcResult mvcResult = mvc.perform(get("/employee/" + EMPLOYEE_ID + "/orderHistory?page=11&target=ORDER_STATUS&direction=ASC"))
                                     //then
                                     .andExpect(status().isOk())
                                     .andExpect(view().name("allCustomerOrderHistory"))
                                     .andExpect(model().attributeExists("orderHistoryPageHandler"))
                                     .andReturn();

            //url 요청에서 전달받은 값
            Collection<OrderHistory> allOrderHistoryItem = (Collection<OrderHistory>) mvcResult.getModelAndView()
                                                                                               .getModel()
                                                                                               .get("orderHistory");

            //데이터베이스에서 전달받은 주문내역과
            //url 요청에서 전달받은 주문내역이 같은지 검증
            Assertions.assertEquals(allOrderHistory.size(), allOrderHistoryItem.size());

            Object[] itemToArray = allOrderHistory.toArray();
            Object[] otherItemToArray = allOrderHistoryItem.toArray();
            boolean allMatch = IntStream.range(0, itemToArray.length)
                                        .allMatch(idx -> itemToArray[idx].equals(otherItemToArray[idx]));

            assertTrue(allMatch);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @DisplayName("49번직원이 주문상태,내림차순,1페이지를 요청할 때 주문내역 아이템을 검증한다.")
    @Test
    public void validatePageHandlerBySearched_orderStatus_DESC_page1()
    {
        try
        {
            //given
            final Integer PAGE = 1;
            final OrderHistorySearchCondition.OrderHistorySortTarget TARGET = OrderHistorySearchCondition.OrderHistorySortTarget.ORDER_STATUS;
            final OrderHistorySearchCondition.OrderHistorySearchConditionBuilder builder = new OrderHistorySearchCondition.OrderHistorySearchConditionBuilder();
            final OrderHistorySearchCondition.OrderHistorySortDirection DIRECTION = OrderHistorySearchCondition.OrderHistorySortDirection.DESC;
            //검색 페이지 : 1
            builder.page(PAGE);
            //정렬대상 : 주문상태
            builder.target(TARGET);
            //정렬방향 : 내림차순
            builder.direction(DIRECTION);
            //검색조건
            OrderHistorySearchCondition searchCondition = builder.build();
            //검색조건에 부합되는 주문내역 데이터(=데이터베이스에서 전달받은 값)
            Collection<OrderHistory> allOrderHistory = (Collection<OrderHistory>) service.pageAndSort(searchCondition);

            //when
            MvcResult mvcResult = mvc.perform(get("/employee/" + EMPLOYEE_ID + "/orderHistory?page=1&target=ORDER_STATUS&direction=DESC"))
                                     //then
                                     .andExpect(status().isOk())
                                     .andExpect(view().name("allCustomerOrderHistory"))
                                     .andExpect(model().attributeExists("orderHistoryPageHandler"))
                                     .andReturn();

            //url 요청에서 전달받은 값
            Collection<OrderHistory> allOrderHistoryItem = (Collection<OrderHistory>) mvcResult.getModelAndView()
                                                                                               .getModel()
                                                                                               .get("orderHistory");

            //데이터베이스에서 전달받은 주문내역과
            //url 요청에서 전달받은 주문내역이 같은지 검증
            Assertions.assertEquals(allOrderHistory.size(), allOrderHistoryItem.size());

            Object[] itemToArray = allOrderHistory.toArray();
            Object[] otherItemToArray = allOrderHistoryItem.toArray();
            boolean allMatch = IntStream.range(0, itemToArray.length)
                                        .allMatch(idx -> itemToArray[idx].equals(otherItemToArray[idx]));

            assertTrue(allMatch);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @DisplayName("49번직원이 주문상태,내림차순,11페이지를 요청할 때 주문내역 아이템을 검증한다.")
    @Test
    public void validatePageHandlerBySearched_orderStatus_DESC_page11()
    {
        try
        {
            //given
            final Integer PAGE = 11;
            final OrderHistorySearchCondition.OrderHistorySortTarget TARGET = OrderHistorySearchCondition.OrderHistorySortTarget.ORDER_STATUS;
            final OrderHistorySearchCondition.OrderHistorySearchConditionBuilder builder = new OrderHistorySearchCondition.OrderHistorySearchConditionBuilder();
            final OrderHistorySearchCondition.OrderHistorySortDirection DIRECTION = OrderHistorySearchCondition.OrderHistorySortDirection.DESC;
            //검색 페이지 : 11
            builder.page(PAGE);
            //정렬대상 : 주문상태
            builder.target(TARGET);
            //정렬방향 : 내림차순
            builder.direction(DIRECTION);
            //검색조건
            OrderHistorySearchCondition searchCondition = builder.build();
            //검색조건에 부합되는 주문내역 데이터(=데이터베이스에서 전달받은 값)
            Collection<OrderHistory> allOrderHistory = (Collection<OrderHistory>) service.pageAndSort(searchCondition);

            //when
            MvcResult mvcResult = mvc.perform(get("/employee/" + EMPLOYEE_ID + "/orderHistory?page=11&target=ORDER_STATUS&direction=DESC"))
                                     //then
                                     .andExpect(status().isOk())
                                     .andExpect(view().name("allCustomerOrderHistory"))
                                     .andExpect(model().attributeExists("orderHistoryPageHandler"))
                                     .andReturn();

            //url 요청에서 전달받은 값
            Collection<OrderHistory> allOrderHistoryItem = (Collection<OrderHistory>) mvcResult.getModelAndView()
                                                                                               .getModel()
                                                                                               .get("orderHistory");

            //데이터베이스에서 전달받은 주문내역과
            //url 요청에서 전달받은 주문내역이 같은지 검증
            Assertions.assertEquals(allOrderHistory.size(), allOrderHistoryItem.size());

            Object[] itemToArray = allOrderHistory.toArray();
            Object[] otherItemToArray = allOrderHistoryItem.toArray();
            boolean allMatch = IntStream.range(0, itemToArray.length)
                                        .allMatch(idx -> itemToArray[idx].equals(otherItemToArray[idx]));

            assertTrue(allMatch);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @DisplayName("49번직원이 주문자,오름차순,1페이지를 요청할 때 주문내역 아이템을 검증한다.")
    @Test
    public void validatePageHandlerBySearched_ORDERER_ASC_page1()
    {
        try
        {
            //given
            final Integer PAGE = 1;
            final OrderHistorySearchCondition.OrderHistorySortTarget TARGET = OrderHistorySearchCondition.OrderHistorySortTarget.ORDERER;
            final OrderHistorySearchCondition.OrderHistorySearchConditionBuilder builder = new OrderHistorySearchCondition.OrderHistorySearchConditionBuilder();
            final OrderHistorySearchCondition.OrderHistorySortDirection DIRECTION = OrderHistorySearchCondition.OrderHistorySortDirection.ASC;
            //검색 페이지 : 1
            builder.page(PAGE);
            //정렬대상 : 주문자
            builder.target(TARGET);
            //정렬방향 : 오름차순
            builder.direction(DIRECTION);
            //검색조건
            OrderHistorySearchCondition searchCondition = builder.build();
            //검색조건에 부합되는 주문내역 데이터(=데이터베이스에서 전달받은 값)
            Collection<OrderHistory> allOrderHistory = (Collection<OrderHistory>) service.pageAndSort(searchCondition);

            //when
            MvcResult mvcResult = mvc.perform(get("/employee/" + EMPLOYEE_ID + "/orderHistory?page=1&target=ORDERER&direction=ASC"))
                                     //then
                                     .andExpect(status().isOk())
                                     .andExpect(view().name("allCustomerOrderHistory"))
                                     .andExpect(model().attributeExists("orderHistoryPageHandler"))
                                     .andReturn();

            //url 요청에서 전달받은 값
            Collection<OrderHistory> allOrderHistoryItem = (Collection<OrderHistory>) mvcResult.getModelAndView()
                                                                                               .getModel()
                                                                                               .get("orderHistory");

            //데이터베이스에서 전달받은 주문내역과
            //url 요청에서 전달받은 주문내역이 같은지 검증
            Assertions.assertEquals(allOrderHistory.size(), allOrderHistoryItem.size());

            Object[] itemToArray = allOrderHistory.toArray();
            Object[] otherItemToArray = allOrderHistoryItem.toArray();
            boolean allMatch = IntStream.range(0, itemToArray.length)
                                        .allMatch(idx -> itemToArray[idx].equals(otherItemToArray[idx]));

            assertTrue(allMatch);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @DisplayName("49번직원이 주문자,오름차순,11페이지를 요청할 때 주문내역 아이템을 검증한다.")
    @Test
    public void validatePageHandlerBySearched_ORDERER_ASC_page11()
    {
        try
        {
            //given
            final Integer PAGE = 11;
            final OrderHistorySearchCondition.OrderHistorySortTarget TARGET = OrderHistorySearchCondition.OrderHistorySortTarget.ORDERER;
            final OrderHistorySearchCondition.OrderHistorySearchConditionBuilder builder = new OrderHistorySearchCondition.OrderHistorySearchConditionBuilder();
            final OrderHistorySearchCondition.OrderHistorySortDirection DIRECTION = OrderHistorySearchCondition.OrderHistorySortDirection.ASC;
            //검색 페이지 : 11
            builder.page(PAGE);
            //정렬대상 : 주문자
            builder.target(TARGET);
            //정렬방향 : 오름차순
            builder.direction(DIRECTION);
            //검색조건
            OrderHistorySearchCondition searchCondition = builder.build();
            //검색조건에 부합되는 주문내역 데이터(=데이터베이스에서 전달받은 값)
            Collection<OrderHistory> allOrderHistory = (Collection<OrderHistory>) service.pageAndSort(searchCondition);

            //when
            MvcResult mvcResult = mvc.perform(get("/employee/" + EMPLOYEE_ID + "/orderHistory?page=11&target=ORDERER&direction=ASC"))
                                     //then
                                     .andExpect(status().isOk())
                                     .andExpect(view().name("allCustomerOrderHistory"))
                                     .andExpect(model().attributeExists("orderHistoryPageHandler"))
                                     .andReturn();

            //url 요청에서 전달받은 값
            Collection<OrderHistory> allOrderHistoryItem = (Collection<OrderHistory>) mvcResult.getModelAndView()
                                                                                               .getModel()
                                                                                               .get("orderHistory");

            //데이터베이스에서 전달받은 주문내역과
            //url 요청에서 전달받은 주문내역이 같은지 검증
            Assertions.assertEquals(allOrderHistory.size(), allOrderHistoryItem.size());

            Object[] itemToArray = allOrderHistory.toArray();
            Object[] otherItemToArray = allOrderHistoryItem.toArray();
            boolean allMatch = IntStream.range(0, itemToArray.length)
                                        .allMatch(idx -> itemToArray[idx].equals(otherItemToArray[idx]));

            assertTrue(allMatch);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @DisplayName("49번직원이 주문자,내림차순,1페이지를 요청할 때 주문내역 아이템을 검증한다.")
    @Test
    public void validatePageHandlerBySearched_ORDERER_DESC_page1()
    {
        try
        {
            //given
            final Integer PAGE = 1;
            final OrderHistorySearchCondition.OrderHistorySortTarget TARGET = OrderHistorySearchCondition.OrderHistorySortTarget.ORDERER;
            final OrderHistorySearchCondition.OrderHistorySearchConditionBuilder builder = new OrderHistorySearchCondition.OrderHistorySearchConditionBuilder();
            final OrderHistorySearchCondition.OrderHistorySortDirection DIRECTION = OrderHistorySearchCondition.OrderHistorySortDirection.DESC;
            //검색 페이지 : 1
            builder.page(PAGE);
            //정렬대상 : 주문자
            builder.target(TARGET);
            //정렬방향 : 내림차순
            builder.direction(DIRECTION);
            //검색조건
            OrderHistorySearchCondition searchCondition = builder.build();
            //검색조건에 부합되는 주문내역 데이터(=데이터베이스에서 전달받은 값)
            Collection<OrderHistory> allOrderHistory = (Collection<OrderHistory>) service.pageAndSort(searchCondition);

            //when
            MvcResult mvcResult = mvc.perform(get("/employee/" + EMPLOYEE_ID + "/orderHistory?page=1&target=ORDERER&direction=DESC"))
                                     //then
                                     .andExpect(status().isOk())
                                     .andExpect(view().name("allCustomerOrderHistory"))
                                     .andExpect(model().attributeExists("orderHistoryPageHandler"))
                                     .andReturn();

            //url 요청에서 전달받은 값
            Collection<OrderHistory> allOrderHistoryItem = (Collection<OrderHistory>) mvcResult.getModelAndView()
                                                                                               .getModel()
                                                                                               .get("orderHistory");

            //데이터베이스에서 전달받은 주문내역과
            //url 요청에서 전달받은 주문내역이 같은지 검증
            Assertions.assertEquals(allOrderHistory.size(), allOrderHistoryItem.size());

            Object[] itemToArray = allOrderHistory.toArray();
            Object[] otherItemToArray = allOrderHistoryItem.toArray();
            boolean allMatch = IntStream.range(0, itemToArray.length)
                                        .allMatch(idx -> itemToArray[idx].equals(otherItemToArray[idx]));

            assertTrue(allMatch);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @DisplayName("49번직원이 주문자,내림차순,11페이지를 요청할 때 주문내역 아이템을 검증한다.")
    @Test
    public void validatePageHandlerBySearched_ORDERER_DESC_page11()
    {
        try
        {
            //given
            final Integer PAGE = 11;
            final OrderHistorySearchCondition.OrderHistorySortTarget TARGET = OrderHistorySearchCondition.OrderHistorySortTarget.ORDERER;
            final OrderHistorySearchCondition.OrderHistorySearchConditionBuilder builder = new OrderHistorySearchCondition.OrderHistorySearchConditionBuilder();
            final OrderHistorySearchCondition.OrderHistorySortDirection DIRECTION = OrderHistorySearchCondition.OrderHistorySortDirection.DESC;
            //검색 페이지 : 11
            builder.page(PAGE);
            //정렬대상 : 주문자
            builder.target(TARGET);
            //정렬방향 : 내림차순
            builder.direction(DIRECTION);
            //검색조건
            OrderHistorySearchCondition searchCondition = builder.build();
            //검색조건에 부합되는 주문내역 데이터(=데이터베이스에서 전달받은 값)
            Collection<OrderHistory> allOrderHistory = (Collection<OrderHistory>) service.pageAndSort(searchCondition);

            //when
            MvcResult mvcResult = mvc.perform(get("/employee/" + EMPLOYEE_ID + "/orderHistory?page=11&target=ORDERER&direction=DESC"))
                                     //then
                                     .andExpect(status().isOk())
                                     .andExpect(view().name("allCustomerOrderHistory"))
                                     .andExpect(model().attributeExists("orderHistoryPageHandler"))
                                     .andReturn();

            //url 요청에서 전달받은 값
            Collection<OrderHistory> allOrderHistoryItem = (Collection<OrderHistory>) mvcResult.getModelAndView()
                                                                                               .getModel()
                                                                                               .get("orderHistory");

            //데이터베이스에서 전달받은 주문내역과
            //url 요청에서 전달받은 주문내역이 같은지 검증
            Assertions.assertEquals(allOrderHistory.size(), allOrderHistoryItem.size());

            Object[] itemToArray = allOrderHistory.toArray();
            Object[] otherItemToArray = allOrderHistoryItem.toArray();
            boolean allMatch = IntStream.range(0, itemToArray.length)
                                        .allMatch(idx -> itemToArray[idx].equals(otherItemToArray[idx]));

            assertTrue(allMatch);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @DisplayName("49번직원이 Kym Hawken(주문자),오름차순,1페이지를 요청할 때 주문내역 아이템을 검증한다.")
    @Test
    public void validatePageHandlerBySearched_Kym_Hawken_ASC_page1()
    {
        try
        {
            //given
            final Integer PAGE = 1;
            final OrderHistorySearchCondition.OrderHistorySortTarget TARGET = OrderHistorySearchCondition.OrderHistorySortTarget.ORDERER;
            final String keyword="Kym Hawken";
            final OrderHistorySearchCondition.OrderHistorySearchConditionBuilder builder = new OrderHistorySearchCondition.OrderHistorySearchConditionBuilder();
            final OrderHistorySearchCondition.OrderHistorySortDirection DIRECTION = OrderHistorySearchCondition.OrderHistorySortDirection.ASC;
            //검색 페이지 : 1
            builder.page(PAGE);
            //정렬대상 : 주문자
            builder.target(TARGET);
            //키워드(주문자) : Kym Hawken
            builder.keyword(keyword);
            //정렬방향 : 오름차순
            builder.direction(DIRECTION);
            //검색조건
            OrderHistorySearchCondition searchCondition = builder.build();
            //검색조건에 부합되는 주문내역 데이터(=데이터베이스에서 전달받은 값)
            Collection<OrderHistory> allOrderHistory = (Collection<OrderHistory>) service.pageAndSort(searchCondition);

            //when
            MvcResult mvcResult = mvc.perform(get("/employee/" + EMPLOYEE_ID + "/orderHistory?page=1&target=ORDERER&keyword=Kym Hawken&direction=ASC"))
                                     //then
                                     .andExpect(status().isOk())
                                     .andExpect(view().name("allCustomerOrderHistory"))
                                     .andExpect(model().attributeExists("orderHistoryPageHandler"))
                                     .andReturn();

            //url 요청에서 전달받은 값
            Collection<OrderHistory> allOrderHistoryItem = (Collection<OrderHistory>) mvcResult.getModelAndView()
                                                                                               .getModel()
                                                                                               .get("orderHistory");

            //데이터베이스에서 전달받은 주문내역과
            //url 요청에서 전달받은 주문내역이 같은지 검증
            Assertions.assertEquals(allOrderHistory.size(), allOrderHistoryItem.size());

            Object[] itemToArray = allOrderHistory.toArray();
            Object[] otherItemToArray = allOrderHistoryItem.toArray();

            boolean allMatch = IntStream.range(0, itemToArray.length)
                                        .allMatch(idx -> itemToArray[idx].equals(otherItemToArray[idx]));

            assertTrue(allMatch);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @DisplayName("49번직원이 Kym Hawken(주문자),오름차순,3페이지를 요청할 때 주문내역 아이템을 검증한다.")
    @Test
    public void validatePageHandlerBySearched_Kym_Hawken_ASC_page3()
    {
        try
        {
            //given
            final Integer PAGE = 3;
            final OrderHistorySearchCondition.OrderHistorySortTarget TARGET = OrderHistorySearchCondition.OrderHistorySortTarget.ORDERER;
            final String keyword="Kym Hawken";
            final OrderHistorySearchCondition.OrderHistorySearchConditionBuilder builder = new OrderHistorySearchCondition.OrderHistorySearchConditionBuilder();
            final OrderHistorySearchCondition.OrderHistorySortDirection DIRECTION = OrderHistorySearchCondition.OrderHistorySortDirection.ASC;
            //검색 페이지 : 3
            builder.page(PAGE);
            //정렬대상 : 주문자
            builder.target(TARGET);
            //키워드(주문자) : Kym Hawken
            builder.keyword(keyword);
            //정렬방향 : 오름차순
            builder.direction(DIRECTION);
            //검색조건
            OrderHistorySearchCondition searchCondition = builder.build();
            //검색조건에 부합되는 주문내역 데이터(=데이터베이스에서 전달받은 값)
            Collection<OrderHistory> allOrderHistory = (Collection<OrderHistory>) service.pageAndSort(searchCondition);

            //when
            MvcResult mvcResult = mvc.perform(get("/employee/" + EMPLOYEE_ID + "/orderHistory?page=3&target=ORDERER&keyword=Kym Hawken&direction=ASC"))
                                     //then
                                     .andExpect(status().isOk())
                                     .andExpect(view().name("allCustomerOrderHistory"))
                                     .andExpect(model().attributeExists("orderHistoryPageHandler"))
                                     .andReturn();

            //url 요청에서 전달받은 값
            Collection<OrderHistory> allOrderHistoryItem = (Collection<OrderHistory>) mvcResult.getModelAndView()
                                                                                               .getModel()
                                                                                               .get("orderHistory");

            //데이터베이스에서 전달받은 주문내역과
            //url 요청에서 전달받은 주문내역이 같은지 검증
            Assertions.assertEquals(allOrderHistory.size(), allOrderHistoryItem.size());

            Object[] itemToArray = allOrderHistory.toArray();
            Object[] otherItemToArray = allOrderHistoryItem.toArray();

            boolean allMatch = IntStream.range(0, itemToArray.length)
                                        .allMatch(idx -> itemToArray[idx].equals(otherItemToArray[idx]));

            assertTrue(allMatch);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @DisplayName("49번직원이 Kym Hawken(주문자),내림차순,1페이지를 요청할 때 주문내역 아이템을 검증한다.")
    @Test
    public void validatePageHandlerBySearched_Kym_Hawken_DESC_page1()
    {
        try
        {
            //given
            final Integer PAGE = 1;
            final OrderHistorySearchCondition.OrderHistorySortTarget TARGET = OrderHistorySearchCondition.OrderHistorySortTarget.ORDERER;
            final String keyword="Kym Hawken";
            final OrderHistorySearchCondition.OrderHistorySearchConditionBuilder builder = new OrderHistorySearchCondition.OrderHistorySearchConditionBuilder();
            final OrderHistorySearchCondition.OrderHistorySortDirection DIRECTION = OrderHistorySearchCondition.OrderHistorySortDirection.DESC;
            //검색 페이지 : 1
            builder.page(PAGE);
            //정렬대상 : 주문자
            builder.target(TARGET);
            //키워드(주문자) : Kym Hawken
            builder.keyword(keyword);
            //정렬방향 : 내림차순
            builder.direction(DIRECTION);
            //검색조건
            OrderHistorySearchCondition searchCondition = builder.build();
            //검색조건에 부합되는 주문내역 데이터(=데이터베이스에서 전달받은 값)
            Collection<OrderHistory> allOrderHistory = (Collection<OrderHistory>) service.pageAndSort(searchCondition);

            //when
            MvcResult mvcResult = mvc.perform(get("/employee/" + EMPLOYEE_ID + "/orderHistory?page=1&target=ORDERER&keyword=Kym Hawken&direction=DESC"))
                                     //then
                                     .andExpect(status().isOk())
                                     .andExpect(view().name("allCustomerOrderHistory"))
                                     .andExpect(model().attributeExists("orderHistoryPageHandler"))
                                     .andReturn();

            //url 요청에서 전달받은 값
            Collection<OrderHistory> allOrderHistoryItem = (Collection<OrderHistory>) mvcResult.getModelAndView()
                                                                                               .getModel()
                                                                                               .get("orderHistory");

            //데이터베이스에서 전달받은 주문내역과
            //url 요청에서 전달받은 주문내역이 같은지 검증
            Assertions.assertEquals(allOrderHistory.size(), allOrderHistoryItem.size());

            Object[] itemToArray = allOrderHistory.toArray();
            Object[] otherItemToArray = allOrderHistoryItem.toArray();

            boolean allMatch = IntStream.range(0, itemToArray.length)
                                        .allMatch(idx -> itemToArray[idx].equals(otherItemToArray[idx]));

            assertTrue(allMatch);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @DisplayName("49번직원이 Kym Hawken(주문자),내림차순,3페이지를 요청할 때 주문내역 아이템을 검증한다.")
    @Test
    public void validatePageHandlerBySearched_Kym_Hawken_DESC_page3()
    {
        try
        {
            //given
            final Integer PAGE = 3;
            final OrderHistorySearchCondition.OrderHistorySortTarget TARGET = OrderHistorySearchCondition.OrderHistorySortTarget.ORDERER;
            final String keyword="Kym Hawken";
            final OrderHistorySearchCondition.OrderHistorySearchConditionBuilder builder = new OrderHistorySearchCondition.OrderHistorySearchConditionBuilder();
            final OrderHistorySearchCondition.OrderHistorySortDirection DIRECTION = OrderHistorySearchCondition.OrderHistorySortDirection.DESC;
            //검색 페이지 : 3
            builder.page(PAGE);
            //정렬대상 : 주문자
            builder.target(TARGET);
            //키워드(주문자) : Kym Hawken
            builder.keyword(keyword);
            //정렬방향 : 내림차순
            builder.direction(DIRECTION);
            //검색조건
            OrderHistorySearchCondition searchCondition = builder.build();
            //검색조건에 부합되는 주문내역 데이터(=데이터베이스에서 전달받은 값)
            Collection<OrderHistory> allOrderHistory = (Collection<OrderHistory>) service.pageAndSort(searchCondition);

            //when
            MvcResult mvcResult = mvc.perform(get("/employee/" + EMPLOYEE_ID + "/orderHistory?page=3&target=ORDERER&keyword=Kym Hawken&direction=DESC"))
                                     //then
                                     .andExpect(status().isOk())
                                     .andExpect(view().name("allCustomerOrderHistory"))
                                     .andExpect(model().attributeExists("orderHistoryPageHandler"))
                                     .andReturn();

            //url 요청에서 전달받은 값
            Collection<OrderHistory> allOrderHistoryItem = (Collection<OrderHistory>) mvcResult.getModelAndView()
                                                                                               .getModel()
                                                                                               .get("orderHistory");

            //데이터베이스에서 전달받은 주문내역과
            //url 요청에서 전달받은 주문내역이 같은지 검증
            Assertions.assertEquals(allOrderHistory.size(), allOrderHistoryItem.size());

            Object[] itemToArray = allOrderHistory.toArray();
            Object[] otherItemToArray = allOrderHistoryItem.toArray();

            boolean allMatch = IntStream.range(0, itemToArray.length)
                                        .allMatch(idx -> itemToArray[idx].equals(otherItemToArray[idx]));

            assertTrue(allMatch);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @DisplayName("49번직원이 보류중(주문상태),오름차순,1페이지를 요청할 때 주문내역 아이템을 검증한다.")
    @Test
    public void validatePageHandlerBySearched_Pending_ASC_page1()
    {
        try
        {
            //given
            final Integer PAGE = 1;
            final OrderHistorySearchCondition.OrderHistorySortTarget TARGET = OrderHistorySearchCondition.OrderHistorySortTarget.ORDER_STATUS;
            final String keyword="Pending";
            final OrderHistorySearchCondition.OrderHistorySearchConditionBuilder builder = new OrderHistorySearchCondition.OrderHistorySearchConditionBuilder();
            final OrderHistorySearchCondition.OrderHistorySortDirection DIRECTION = OrderHistorySearchCondition.OrderHistorySortDirection.ASC;
            //검색 페이지 : 1
            builder.page(PAGE);
            //정렬대상 : 주문상태
            builder.target(TARGET);
            //키워드(주문상태) : Pending(=보류중)
            builder.keyword(keyword);
            //정렬방향 : 오름차순
            builder.direction(DIRECTION);
            //검색조건
            OrderHistorySearchCondition searchCondition = builder.build();
            //검색조건에 부합되는 주문내역 데이터(=데이터베이스에서 전달받은 값)
            Collection<OrderHistory> allOrderHistory = (Collection<OrderHistory>) service.pageAndSort(searchCondition);

            //when
            MvcResult mvcResult = mvc.perform(get("/employee/" + EMPLOYEE_ID + "/orderHistory?page=1&target=ORDER_STATUS&keyword=Pending&direction=ASC"))
                                     //then
                                     .andExpect(status().isOk())
                                     .andExpect(view().name("allCustomerOrderHistory"))
                                     .andExpect(model().attributeExists("orderHistoryPageHandler"))
                                     .andReturn();

            //url 요청에서 전달받은 값
            Collection<OrderHistory> allOrderHistoryItem = (Collection<OrderHistory>) mvcResult.getModelAndView()
                                                                                               .getModel()
                                                                                               .get("orderHistory");

            //데이터베이스에서 전달받은 주문내역과
            //url 요청에서 전달받은 주문내역이 같은지 검증
            Assertions.assertEquals(allOrderHistory.size(), allOrderHistoryItem.size());

            Object[] itemToArray = allOrderHistory.toArray();
            Object[] otherItemToArray = allOrderHistoryItem.toArray();

            boolean allMatch = IntStream.range(0, itemToArray.length)
                                        .allMatch(idx -> itemToArray[idx].equals(otherItemToArray[idx]));

            assertTrue(allMatch);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @DisplayName("49번직원이 보류중(주문상태),오름차순,10페이지를 요청할 때 주문내역 아이템을 검증한다.")
    @Test
    public void validatePageHandlerBySearched_Pending_ASC_page10()
    {
        try
        {
            //given
            final Integer PAGE = 10;
            final OrderHistorySearchCondition.OrderHistorySortTarget TARGET = OrderHistorySearchCondition.OrderHistorySortTarget.ORDER_STATUS;
            final OrderHistorySearchCondition.OrderHistoryOrderStatus STATUS = OrderHistorySearchCondition.OrderHistoryOrderStatus.Pending;
            final OrderHistorySearchCondition.OrderHistorySearchConditionBuilder builder = new OrderHistorySearchCondition.OrderHistorySearchConditionBuilder();
            final OrderHistorySearchCondition.OrderHistorySortDirection DIRECTION = OrderHistorySearchCondition.OrderHistorySortDirection.ASC;
            //검색 페이지 : 10
            builder.page(PAGE);
            //정렬대상 : 주문상태
            builder.target(TARGET);
            //주문상태 : Pending(=보류중)
            builder.status(STATUS);
            //정렬방향 : 오름차순
            builder.direction(DIRECTION);
            //검색조건
            OrderHistorySearchCondition searchCondition = builder.build();
            //검색조건에 부합되는 주문내역 데이터(=데이터베이스에서 전달받은 값)
            Collection<OrderHistory> allOrderHistory = (Collection<OrderHistory>) service.pageAndSort(searchCondition);

            //when
            MvcResult mvcResult = mvc.perform(get("/employee/" + EMPLOYEE_ID + "/orderHistory?page=10&target=ORDER_STATUS&status=Pending&direction=ASC"))
                                     //then
                                     .andExpect(status().isOk())
                                     .andExpect(view().name("allCustomerOrderHistory"))
                                     .andExpect(model().attributeExists("orderHistoryPageHandler"))
                                     .andReturn();

            //url 요청에서 전달받은 값
            Collection<OrderHistory> allOrderHistoryItem = (Collection<OrderHistory>) mvcResult.getModelAndView()
                                                                                               .getModel()
                                                                                               .get("orderHistory");

            //데이터베이스에서 전달받은 주문내역과
            //url 요청에서 전달받은 주문내역이 같은지 검증
            Assertions.assertEquals(allOrderHistory.size(), allOrderHistoryItem.size());

            Object[] itemToArray = allOrderHistory.toArray();
            Object[] otherItemToArray = allOrderHistoryItem.toArray();

            boolean allMatch = IntStream.range(0, itemToArray.length)
                                        .allMatch(idx -> itemToArray[idx].equals(otherItemToArray[idx]));

            assertTrue(allMatch);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @DisplayName("49번직원이 보류중(주문상태),내림차순,1페이지를 요청할 때 주문내역 아이템을 검증한다.")
    @Test
    public void validatePageHandlerBySearched_Pending_DESC_page1()
    {
        try
        {
            //given
            final Integer PAGE = 1;
            final OrderHistorySearchCondition.OrderHistorySortTarget TARGET = OrderHistorySearchCondition.OrderHistorySortTarget.ORDER_STATUS;
            final OrderHistorySearchCondition.OrderHistoryOrderStatus STATUS = OrderHistorySearchCondition.OrderHistoryOrderStatus.Pending;
            final OrderHistorySearchCondition.OrderHistorySearchConditionBuilder builder = new OrderHistorySearchCondition.OrderHistorySearchConditionBuilder();
            final OrderHistorySearchCondition.OrderHistorySortDirection DIRECTION = OrderHistorySearchCondition.OrderHistorySortDirection.DESC;
            //검색 페이지 : 1
            builder.page(PAGE);
            //정렬대상 : 주문상태
            builder.target(TARGET);
            //주문상태 : Pending(=보류중)
            builder.status(STATUS);
            //정렬방향 : 내림차순
            builder.direction(DIRECTION);
            //검색조건
            OrderHistorySearchCondition searchCondition = builder.build();
            //검색조건에 부합되는 주문내역 데이터(=데이터베이스에서 전달받은 값)
            Collection<OrderHistory> allOrderHistory = (Collection<OrderHistory>) service.pageAndSort(searchCondition);

            //when
            MvcResult mvcResult = mvc.perform(get("/employee/" + EMPLOYEE_ID + "/orderHistory?page=1&target=ORDER_STATUS&status=Pending&direction=DESC"))
                                     //then
                                     .andExpect(status().isOk())
                                     .andExpect(view().name("allCustomerOrderHistory"))
                                     .andExpect(model().attributeExists("orderHistoryPageHandler"))
                                     .andReturn();

            //url 요청에서 전달받은 값
            Collection<OrderHistory> allOrderHistoryItem = (Collection<OrderHistory>) mvcResult.getModelAndView()
                                                                                               .getModel()
                                                                                               .get("orderHistory");

            //데이터베이스에서 전달받은 주문내역과
            //url 요청에서 전달받은 주문내역이 같은지 검증
            Assertions.assertEquals(allOrderHistory.size(), allOrderHistoryItem.size());

            Object[] itemToArray = allOrderHistory.toArray();
            Object[] otherItemToArray = allOrderHistoryItem.toArray();

            boolean allMatch = IntStream.range(0, itemToArray.length)
                                        .allMatch(idx -> itemToArray[idx].equals(otherItemToArray[idx]));

            assertTrue(allMatch);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @DisplayName("49번직원이 보류중(주문상태),내림차순,10페이지를 요청할 때 주문내역 아이템을 검증한다.")
    @Test
    public void validatePageHandlerBySearched_Pending_DESC_page10()
    {
        try
        {
            //given
            final Integer PAGE = 10;
            final OrderHistorySearchCondition.OrderHistorySortTarget TARGET = OrderHistorySearchCondition.OrderHistorySortTarget.ORDER_STATUS;
            final OrderHistorySearchCondition.OrderHistoryOrderStatus STATUS = OrderHistorySearchCondition.OrderHistoryOrderStatus.Pending;
            final OrderHistorySearchCondition.OrderHistorySearchConditionBuilder builder = new OrderHistorySearchCondition.OrderHistorySearchConditionBuilder();
            final OrderHistorySearchCondition.OrderHistorySortDirection DIRECTION = OrderHistorySearchCondition.OrderHistorySortDirection.DESC;
            //검색 페이지 : 10
            builder.page(PAGE);
            //정렬대상 : 주문상태
            builder.target(TARGET);
            //주문상태 : Pending(=보류중)
            builder.status(STATUS);
            //정렬방향 : 내림차순
            builder.direction(DIRECTION);
            //검색조건
            OrderHistorySearchCondition searchCondition = builder.build();
            //검색조건에 부합되는 주문내역 데이터(=데이터베이스에서 전달받은 값)
            Collection<OrderHistory> allOrderHistory = (Collection<OrderHistory>) service.pageAndSort(searchCondition);

            //when
            MvcResult mvcResult = mvc.perform(get("/employee/" + EMPLOYEE_ID + "/orderHistory?page=10&target=ORDER_STATUS&status=Pending&direction=DESC"))
                                     //then
                                     .andExpect(status().isOk())
                                     .andExpect(view().name("allCustomerOrderHistory"))
                                     .andExpect(model().attributeExists("orderHistoryPageHandler"))
                                     .andReturn();

            //url 요청에서 전달받은 값
            Collection<OrderHistory> allOrderHistoryItem = (Collection<OrderHistory>) mvcResult.getModelAndView()
                                                                                               .getModel()
                                                                                               .get("orderHistory");

            //데이터베이스에서 전달받은 주문내역과
            //url 요청에서 전달받은 주문내역이 같은지 검증
            Assertions.assertEquals(allOrderHistory.size(), allOrderHistoryItem.size());

            Object[] itemToArray = allOrderHistory.toArray();
            Object[] otherItemToArray = allOrderHistoryItem.toArray();

            boolean allMatch = IntStream.range(0, itemToArray.length)
                                        .allMatch(idx -> itemToArray[idx].equals(otherItemToArray[idx]));

            assertTrue(allMatch);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @DisplayName("49번직원이 취소됨(주문상태),오름차순,1페이지를 요청할 때 주문내역 아이템을 검증한다.")
    @Test
    public void validatePageHandlerBySearched_Canceled_ASC_page1()
    {
        try
        {
            //given
            final Integer PAGE = 1;
            final OrderHistorySearchCondition.OrderHistorySortTarget TARGET = OrderHistorySearchCondition.OrderHistorySortTarget.ORDER_STATUS;
            final OrderHistorySearchCondition.OrderHistoryOrderStatus STATUS = OrderHistorySearchCondition.OrderHistoryOrderStatus.Canceled;
            final OrderHistorySearchCondition.OrderHistorySearchConditionBuilder builder = new OrderHistorySearchCondition.OrderHistorySearchConditionBuilder();
            final OrderHistorySearchCondition.OrderHistorySortDirection DIRECTION = OrderHistorySearchCondition.OrderHistorySortDirection.ASC;
            //검색 페이지 : 1
            builder.page(PAGE);
            //정렬대상 : 주문상태
            builder.target(TARGET);
            //주문상태 : Canceled(=취소됨)
            builder.status(STATUS);
            //정렬방향 : 오름차순
            builder.direction(DIRECTION);
            //검색조건
            OrderHistorySearchCondition searchCondition = builder.build();
            //검색조건에 부합되는 주문내역 데이터(=데이터베이스에서 전달받은 값)
            Collection<OrderHistory> allOrderHistory = (Collection<OrderHistory>) service.pageAndSort(searchCondition);

            //when
            MvcResult mvcResult = mvc.perform(get("/employee/" + EMPLOYEE_ID + "/orderHistory?page=1&target=ORDER_STATUS&status=Canceled&direction=ASC"))
                                     //then
                                     .andExpect(status().isOk())
                                     .andExpect(view().name("allCustomerOrderHistory"))
                                     .andExpect(model().attributeExists("orderHistoryPageHandler"))
                                     .andReturn();

            //url 요청에서 전달받은 값
            Collection<OrderHistory> allOrderHistoryItem = (Collection<OrderHistory>) mvcResult.getModelAndView()
                                                                                               .getModel()
                                                                                               .get("orderHistory");

            //데이터베이스에서 전달받은 주문내역과
            //url 요청에서 전달받은 주문내역이 같은지 검증
            Assertions.assertEquals(allOrderHistory.size(), allOrderHistoryItem.size());

            Object[] itemToArray = allOrderHistory.toArray();
            Object[] otherItemToArray = allOrderHistoryItem.toArray();

            boolean allMatch = IntStream.range(0, itemToArray.length)
                                        .allMatch(idx -> itemToArray[idx].equals(otherItemToArray[idx]));

            assertTrue(allMatch);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @DisplayName("49번직원이 취소됨(주문상태),오름차순,10페이지를 요청할 때 주문내역 아이템을 검증한다.")
    @Test
    public void validatePageHandlerBySearched_Canceled_ASC_page10()
    {
        try
        {
            //given
            final Integer PAGE = 10;
            final OrderHistorySearchCondition.OrderHistorySortTarget TARGET = OrderHistorySearchCondition.OrderHistorySortTarget.ORDER_STATUS;
            final OrderHistorySearchCondition.OrderHistoryOrderStatus STATUS = OrderHistorySearchCondition.OrderHistoryOrderStatus.Canceled;
            final OrderHistorySearchCondition.OrderHistorySearchConditionBuilder builder = new OrderHistorySearchCondition.OrderHistorySearchConditionBuilder();
            final OrderHistorySearchCondition.OrderHistorySortDirection DIRECTION = OrderHistorySearchCondition.OrderHistorySortDirection.ASC;
            //검색 페이지 : 10
            builder.page(PAGE);
            //정렬대상 : 주문상태
            builder.target(TARGET);
            //주문상태 : Canceled(=취소됨)
            builder.status(STATUS);
            //정렬방향 : 오름차순
            builder.direction(DIRECTION);
            //검색조건
            OrderHistorySearchCondition searchCondition = builder.build();
            //검색조건에 부합되는 주문내역 데이터(=데이터베이스에서 전달받은 값)
            Collection<OrderHistory> allOrderHistory = (Collection<OrderHistory>) service.pageAndSort(searchCondition);

            //when
            MvcResult mvcResult = mvc.perform(get("/employee/" + EMPLOYEE_ID + "/orderHistory?page=10&target=ORDER_STATUS&status=Canceled&direction=ASC"))
                                     //then
                                     .andExpect(status().isOk())
                                     .andExpect(view().name("allCustomerOrderHistory"))
                                     .andExpect(model().attributeExists("orderHistoryPageHandler"))
                                     .andReturn();

            //url 요청에서 전달받은 값
            Collection<OrderHistory> allOrderHistoryItem = (Collection<OrderHistory>) mvcResult.getModelAndView()
                                                                                               .getModel()
                                                                                               .get("orderHistory");

            //데이터베이스에서 전달받은 주문내역과
            //url 요청에서 전달받은 주문내역이 같은지 검증
            Assertions.assertEquals(allOrderHistory.size(), allOrderHistoryItem.size());

            Object[] itemToArray = allOrderHistory.toArray();
            Object[] otherItemToArray = allOrderHistoryItem.toArray();

            boolean allMatch = IntStream.range(0, itemToArray.length)
                                        .allMatch(idx -> itemToArray[idx].equals(otherItemToArray[idx]));

            assertTrue(allMatch);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @DisplayName("49번직원이 취소됨(주문상태),내림차순,1페이지를 요청할 때 주문내역 아이템을 검증한다.")
    @Test
    public void validatePageHandlerBySearched_Canceled_DESC_page1()
    {
        try
        {
            //given
            final Integer PAGE = 1;
            final OrderHistorySearchCondition.OrderHistorySortTarget TARGET = OrderHistorySearchCondition.OrderHistorySortTarget.ORDER_STATUS;
            final OrderHistorySearchCondition.OrderHistoryOrderStatus STATUS = OrderHistorySearchCondition.OrderHistoryOrderStatus.Canceled;
            final OrderHistorySearchCondition.OrderHistorySearchConditionBuilder builder = new OrderHistorySearchCondition.OrderHistorySearchConditionBuilder();
            final OrderHistorySearchCondition.OrderHistorySortDirection DIRECTION = OrderHistorySearchCondition.OrderHistorySortDirection.DESC;
            //검색 페이지 : 1
            builder.page(PAGE);
            //정렬대상 : 주문상태
            builder.target(TARGET);
            //주문상태 : Canceled(=취소됨)
            builder.status(STATUS);
            //정렬방향 : 오름차순
            builder.direction(DIRECTION);
            //검색조건
            OrderHistorySearchCondition searchCondition = builder.build();
            //검색조건에 부합되는 주문내역 데이터(=데이터베이스에서 전달받은 값)
            Collection<OrderHistory> allOrderHistory = (Collection<OrderHistory>) service.pageAndSort(searchCondition);

            //when
            MvcResult mvcResult = mvc.perform(get("/employee/" + EMPLOYEE_ID + "/orderHistory?page=1&target=ORDER_STATUS&status=Canceled&direction=DESC"))
                                     //then
                                     .andExpect(status().isOk())
                                     .andExpect(view().name("allCustomerOrderHistory"))
                                     .andExpect(model().attributeExists("orderHistoryPageHandler"))
                                     .andReturn();

            //url 요청에서 전달받은 값
            Collection<OrderHistory> allOrderHistoryItem = (Collection<OrderHistory>) mvcResult.getModelAndView()
                                                                                               .getModel()
                                                                                               .get("orderHistory");

            //데이터베이스에서 전달받은 주문내역과
            //url 요청에서 전달받은 주문내역이 같은지 검증
            Assertions.assertEquals(allOrderHistory.size(), allOrderHistoryItem.size());

            Object[] itemToArray = allOrderHistory.toArray();
            Object[] otherItemToArray = allOrderHistoryItem.toArray();

            boolean allMatch = IntStream.range(0, itemToArray.length)
                                        .allMatch(idx -> itemToArray[idx].equals(otherItemToArray[idx]));

            assertTrue(allMatch);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @DisplayName("49번직원이 취소됨(주문상태),내림차순,10페이지를 요청할 때 주문내역 아이템을 검증한다.")
    @Test
    public void validatePageHandlerBySearched_Canceled_DESC_page10()
    {
        try
        {
            //given
            final Integer PAGE = 10;
            final OrderHistorySearchCondition.OrderHistorySortTarget TARGET = OrderHistorySearchCondition.OrderHistorySortTarget.ORDER_STATUS;
            final OrderHistorySearchCondition.OrderHistoryOrderStatus STATUS = OrderHistorySearchCondition.OrderHistoryOrderStatus.Canceled;
            final OrderHistorySearchCondition.OrderHistorySearchConditionBuilder builder = new OrderHistorySearchCondition.OrderHistorySearchConditionBuilder();
            final OrderHistorySearchCondition.OrderHistorySortDirection DIRECTION = OrderHistorySearchCondition.OrderHistorySortDirection.DESC;
            //검색 페이지 : 10
            builder.page(PAGE);
            //정렬대상 : 주문상태
            builder.target(TARGET);
            //주문상태 : Canceled(=취소됨)
            builder.status(STATUS);
            //정렬방향 : 오름차순
            builder.direction(DIRECTION);
            //검색조건
            OrderHistorySearchCondition searchCondition = builder.build();
            //검색조건에 부합되는 주문내역 데이터(=데이터베이스에서 전달받은 값)
            Collection<OrderHistory> allOrderHistory = (Collection<OrderHistory>) service.pageAndSort(searchCondition);

            //when
            MvcResult mvcResult = mvc.perform(get("/employee/" + EMPLOYEE_ID + "/orderHistory?page=10&target=ORDER_STATUS&status=Canceled&direction=DESC"))
                                     //then
                                     .andExpect(status().isOk())
                                     .andExpect(view().name("allCustomerOrderHistory"))
                                     .andExpect(model().attributeExists("orderHistoryPageHandler"))
                                     .andReturn();

            //url 요청에서 전달받은 값
            Collection<OrderHistory> allOrderHistoryItem = (Collection<OrderHistory>) mvcResult.getModelAndView()
                                                                                               .getModel()
                                                                                               .get("orderHistory");

            //데이터베이스에서 전달받은 주문내역과
            //url 요청에서 전달받은 주문내역이 같은지 검증
            Assertions.assertEquals(allOrderHistory.size(), allOrderHistoryItem.size());

            Object[] itemToArray = allOrderHistory.toArray();
            Object[] otherItemToArray = allOrderHistoryItem.toArray();

            boolean allMatch = IntStream.range(0, itemToArray.length)
                                        .allMatch(idx -> itemToArray[idx].equals(otherItemToArray[idx]));

            assertTrue(allMatch);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @DisplayName("49번직원이 출하됨(주문상태),오름차순,1페이지를 요청할 때 주문내역 아이템을 검증한다.")
    @Test
    public void validatePageHandlerBySearched_Shipped_ASC_page1()
    {
        try
        {
            //given
            final Integer PAGE = 1;
            final OrderHistorySearchCondition.OrderHistorySortTarget TARGET = OrderHistorySearchCondition.OrderHistorySortTarget.ORDER_STATUS;
            final OrderHistorySearchCondition.OrderHistoryOrderStatus STATUS = OrderHistorySearchCondition.OrderHistoryOrderStatus.Shipped;
            final OrderHistorySearchCondition.OrderHistorySearchConditionBuilder builder = new OrderHistorySearchCondition.OrderHistorySearchConditionBuilder();
            final OrderHistorySearchCondition.OrderHistorySortDirection DIRECTION = OrderHistorySearchCondition.OrderHistorySortDirection.ASC;
            //검색 페이지 : 1
            builder.page(PAGE);
            //정렬대상 : 주문상태
            builder.target(TARGET);
            //주문상태 : Shipped(=출하됨)
            builder.status(STATUS);
            //정렬방향 : 오름차순
            builder.direction(DIRECTION);
            //검색조건
            OrderHistorySearchCondition searchCondition = builder.build();
            //검색조건에 부합되는 주문내역 데이터(=데이터베이스에서 전달받은 값)
            Collection<OrderHistory> allOrderHistory = (Collection<OrderHistory>) service.pageAndSort(searchCondition);

            //when
            MvcResult mvcResult = mvc.perform(get("/employee/" + EMPLOYEE_ID + "/orderHistory?page=1&target=ORDER_STATUS&status=Shipped&direction=ASC"))
                                     //then
                                     .andExpect(status().isOk())
                                     .andExpect(view().name("allCustomerOrderHistory"))
                                     .andExpect(model().attributeExists("orderHistoryPageHandler"))
                                     .andReturn();

            //url 요청에서 전달받은 값
            Collection<OrderHistory> allOrderHistoryItem = (Collection<OrderHistory>) mvcResult.getModelAndView()
                                                                                               .getModel()
                                                                                               .get("orderHistory");

            //데이터베이스에서 전달받은 주문내역과
            //url 요청에서 전달받은 주문내역이 같은지 검증
            Assertions.assertEquals(allOrderHistory.size(), allOrderHistoryItem.size());

            Object[] itemToArray = allOrderHistory.toArray();
            Object[] otherItemToArray = allOrderHistoryItem.toArray();

            boolean allMatch = IntStream.range(0, itemToArray.length)
                                        .allMatch(idx -> itemToArray[idx].equals(otherItemToArray[idx]));

            assertTrue(allMatch);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @DisplayName("49번직원이 출하됨(주문상태),오름차순,5페이지를 요청할 때 주문내역 아이템을 검증한다.")
    @Test
    public void validatePageHandlerBySearched_Shipped_ASC_page5()
    {
        try
        {
            //given
            final Integer PAGE = 5;
            final OrderHistorySearchCondition.OrderHistorySortTarget TARGET = OrderHistorySearchCondition.OrderHistorySortTarget.ORDER_STATUS;
            final OrderHistorySearchCondition.OrderHistoryOrderStatus STATUS = OrderHistorySearchCondition.OrderHistoryOrderStatus.Shipped;
            final OrderHistorySearchCondition.OrderHistorySearchConditionBuilder builder = new OrderHistorySearchCondition.OrderHistorySearchConditionBuilder();
            final OrderHistorySearchCondition.OrderHistorySortDirection DIRECTION = OrderHistorySearchCondition.OrderHistorySortDirection.ASC;
            //검색 페이지 : 5
            builder.page(PAGE);
            //정렬대상 : 주문상태
            builder.target(TARGET);
            //주문상태 : Shipped(=출하됨)
            builder.status(STATUS);
            //정렬방향 : 오름차순
            builder.direction(DIRECTION);
            //검색조건
            OrderHistorySearchCondition searchCondition = builder.build();
            //검색조건에 부합되는 주문내역 데이터(=데이터베이스에서 전달받은 값)
            Collection<OrderHistory> allOrderHistory = (Collection<OrderHistory>) service.pageAndSort(searchCondition);

            //when
            MvcResult mvcResult = mvc.perform(get("/employee/" + EMPLOYEE_ID + "/orderHistory?page=5&target=ORDER_STATUS&status=Shipped&direction=ASC"))
                                     //then
                                     .andExpect(status().isOk())
                                     .andExpect(view().name("allCustomerOrderHistory"))
                                     .andExpect(model().attributeExists("orderHistoryPageHandler"))
                                     .andReturn();

            //url 요청에서 전달받은 값
            Collection<OrderHistory> allOrderHistoryItem = (Collection<OrderHistory>) mvcResult.getModelAndView()
                                                                                               .getModel()
                                                                                               .get("orderHistory");

            //데이터베이스에서 전달받은 주문내역과
            //url 요청에서 전달받은 주문내역이 같은지 검증
            Assertions.assertEquals(allOrderHistory.size(), allOrderHistoryItem.size());

            Object[] itemToArray = allOrderHistory.toArray();
            Object[] otherItemToArray = allOrderHistoryItem.toArray();

            boolean allMatch = IntStream.range(0, itemToArray.length)
                                        .allMatch(idx -> itemToArray[idx].equals(otherItemToArray[idx]));

            assertTrue(allMatch);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @DisplayName("49번직원이 출하됨(주문상태),내림차순,1페이지를 요청할 때 주문내역 아이템을 검증한다.")
    @Test
    public void validatePageHandlerBySearched_Shipped_DESC_page1()
    {
        try
        {
            //given
            final Integer PAGE = 1;
            final OrderHistorySearchCondition.OrderHistorySortTarget TARGET = OrderHistorySearchCondition.OrderHistorySortTarget.ORDER_STATUS;
            final OrderHistorySearchCondition.OrderHistoryOrderStatus STATUS = OrderHistorySearchCondition.OrderHistoryOrderStatus.Shipped;
            final OrderHistorySearchCondition.OrderHistorySearchConditionBuilder builder = new OrderHistorySearchCondition.OrderHistorySearchConditionBuilder();
            final OrderHistorySearchCondition.OrderHistorySortDirection DIRECTION = OrderHistorySearchCondition.OrderHistorySortDirection.DESC;
            //검색 페이지 : 1
            builder.page(PAGE);
            //정렬대상 : 주문상태
            builder.target(TARGET);
            //주문상태 : Shipped(=출하됨)
            builder.status(STATUS);
            //정렬방향 : 내림차순
            builder.direction(DIRECTION);
            //검색조건
            OrderHistorySearchCondition searchCondition = builder.build();
            //검색조건에 부합되는 주문내역 데이터(=데이터베이스에서 전달받은 값)
            Collection<OrderHistory> allOrderHistory = (Collection<OrderHistory>) service.pageAndSort(searchCondition);

            //when
            MvcResult mvcResult = mvc.perform(get("/employee/" + EMPLOYEE_ID + "/orderHistory?page=1&target=ORDER_STATUS&status=Shipped&direction=DESC"))
                                     //then
                                     .andExpect(status().isOk())
                                     .andExpect(view().name("allCustomerOrderHistory"))
                                     .andExpect(model().attributeExists("orderHistoryPageHandler"))
                                     .andReturn();

            //url 요청에서 전달받은 값
            Collection<OrderHistory> allOrderHistoryItem = (Collection<OrderHistory>) mvcResult.getModelAndView()
                                                                                               .getModel()
                                                                                               .get("orderHistory");

            //데이터베이스에서 전달받은 주문내역과
            //url 요청에서 전달받은 주문내역이 같은지 검증
            Assertions.assertEquals(allOrderHistory.size(), allOrderHistoryItem.size());

            Object[] itemToArray = allOrderHistory.toArray();
            Object[] otherItemToArray = allOrderHistoryItem.toArray();

            boolean allMatch = IntStream.range(0, itemToArray.length)
                                        .allMatch(idx -> itemToArray[idx].equals(otherItemToArray[idx]));

            assertTrue(allMatch);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @DisplayName("49번직원이 출하됨(주문상태),내림차순,5페이지를 요청할 때 주문내역 아이템을 검증한다.")
    @Test
    public void validatePageHandlerBySearched_Shipped_DESC_page5()
    {
        try
        {
            //given
            final Integer PAGE = 5;
            final OrderHistorySearchCondition.OrderHistorySortTarget TARGET = OrderHistorySearchCondition.OrderHistorySortTarget.ORDER_STATUS;
            final OrderHistorySearchCondition.OrderHistoryOrderStatus STATUS = OrderHistorySearchCondition.OrderHistoryOrderStatus.Shipped;
            final OrderHistorySearchCondition.OrderHistorySearchConditionBuilder builder = new OrderHistorySearchCondition.OrderHistorySearchConditionBuilder();
            final OrderHistorySearchCondition.OrderHistorySortDirection DIRECTION = OrderHistorySearchCondition.OrderHistorySortDirection.DESC;
            //검색 페이지 : 5
            builder.page(PAGE);
            //정렬대상 : 주문상태
            builder.target(TARGET);
            //주문상태 : Shipped(=출하됨)
            builder.status(STATUS);
            //정렬방향 : 내림차순
            builder.direction(DIRECTION);
            //검색조건
            OrderHistorySearchCondition searchCondition = builder.build();
            //검색조건에 부합되는 주문내역 데이터(=데이터베이스에서 전달받은 값)
            Collection<OrderHistory> allOrderHistory = (Collection<OrderHistory>) service.pageAndSort(searchCondition);

            //when
            MvcResult mvcResult = mvc.perform(get("/employee/" + EMPLOYEE_ID + "/orderHistory?page=5&target=ORDER_STATUS&status=Shipped&direction=DESC"))
                                     //then
                                     .andExpect(status().isOk())
                                     .andExpect(view().name("allCustomerOrderHistory"))
                                     .andExpect(model().attributeExists("orderHistoryPageHandler"))
                                     .andReturn();

            //url 요청에서 전달받은 값
            Collection<OrderHistory> allOrderHistoryItem = (Collection<OrderHistory>) mvcResult.getModelAndView()
                                                                                               .getModel()
                                                                                               .get("orderHistory");

            //데이터베이스에서 전달받은 주문내역과
            //url 요청에서 전달받은 주문내역이 같은지 검증
            Assertions.assertEquals(allOrderHistory.size(), allOrderHistoryItem.size());

            Object[] itemToArray = allOrderHistory.toArray();
            Object[] otherItemToArray = allOrderHistoryItem.toArray();

            boolean allMatch = IntStream.range(0, itemToArray.length)
                                        .allMatch(idx -> itemToArray[idx].equals(otherItemToArray[idx]));

            assertTrue(allMatch);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @DisplayName("49번직원이 20161117(주문날짜),오름차순,1페이지를 요청할 때 주문내역 아이템을 검증한다.")
    @Test
    public void validatePageHandlerBySearched_20161117_ASC_page1()
    {
        try
        {
            //given
            final Integer PAGE = 1;
            final OrderHistorySearchCondition.OrderHistorySortTarget TARGET = OrderHistorySearchCondition.OrderHistorySortTarget.ORDER_STATUS;
            final String keyword="20161117";
            final OrderHistorySearchCondition.OrderHistorySearchConditionBuilder builder = new OrderHistorySearchCondition.OrderHistorySearchConditionBuilder();
            final OrderHistorySearchCondition.OrderHistorySortDirection DIRECTION = OrderHistorySearchCondition.OrderHistorySortDirection.ASC;
            //검색 페이지 : 1
            builder.page(PAGE);
            //정렬대상 : 주문날짜
            builder.target(TARGET);
            //키워드 : 20161117
            builder.keyword(keyword);
            //정렬방향 : 오름차순
            builder.direction(DIRECTION);
            //검색조건
            OrderHistorySearchCondition searchCondition = builder.build();
            //검색조건에 부합되는 주문내역 데이터(=데이터베이스에서 전달받은 값)
            Collection<OrderHistory> allOrderHistory = (Collection<OrderHistory>) service.pageAndSort(searchCondition);

            //when
            MvcResult mvcResult = mvc.perform(get("/employee/" + EMPLOYEE_ID + "/orderHistory?page=1&target=ORDER_STATUS&keyword=20161117&direction=ASC"))
                                     //then
                                     .andExpect(status().isOk())
                                     .andExpect(view().name("allCustomerOrderHistory"))
                                     .andExpect(model().attributeExists("orderHistoryPageHandler"))
                                     .andReturn();

            //url 요청에서 전달받은 값
            Collection<OrderHistory> allOrderHistoryItem = (Collection<OrderHistory>) mvcResult.getModelAndView()
                                                                                               .getModel()
                                                                                               .get("orderHistory");

            //데이터베이스에서 전달받은 주문내역과
            //url 요청에서 전달받은 주문내역이 같은지 검증
            Assertions.assertEquals(allOrderHistory.size(), allOrderHistoryItem.size());

            Object[] itemToArray = allOrderHistory.toArray();
            Object[] otherItemToArray = allOrderHistoryItem.toArray();

            boolean allMatch = IntStream.range(0, itemToArray.length)
                                        .allMatch(idx -> itemToArray[idx].equals(otherItemToArray[idx]));

            assertTrue(allMatch);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @DisplayName("49번직원이 20161117(주문날짜),내림차순,1페이지를 요청할 때 주문내역 아이템을 검증한다.")
    @Test
    public void validatePageHandlerBySearched_20161117_DESC_page1()
    {
        try
        {
            //given
            final Integer PAGE = 1;
            final OrderHistorySearchCondition.OrderHistorySortTarget TARGET = OrderHistorySearchCondition.OrderHistorySortTarget.ORDER_STATUS;
            final String keyword="20161117";
            final OrderHistorySearchCondition.OrderHistorySearchConditionBuilder builder = new OrderHistorySearchCondition.OrderHistorySearchConditionBuilder();
            final OrderHistorySearchCondition.OrderHistorySortDirection DIRECTION = OrderHistorySearchCondition.OrderHistorySortDirection.DESC;
            //검색 페이지 : 1
            builder.page(PAGE);
            //정렬대상 : 주문날짜
            builder.target(TARGET);
            //키워드 : 20161117
            builder.keyword(keyword);
            //정렬방향 : 오름차순
            builder.direction(DIRECTION);
            //검색조건
            OrderHistorySearchCondition searchCondition = builder.build();
            //검색조건에 부합되는 주문내역 데이터(=데이터베이스에서 전달받은 값)
            Collection<OrderHistory> allOrderHistory = (Collection<OrderHistory>) service.pageAndSort(searchCondition);

            //when
            MvcResult mvcResult = mvc.perform(get("/employee/" + EMPLOYEE_ID + "/orderHistory?page=1&target=ORDER_STATUS&keyword=20161117&direction=DESC"))
                                     //then
                                     .andExpect(status().isOk())
                                     .andExpect(view().name("allCustomerOrderHistory"))
                                     .andExpect(model().attributeExists("orderHistoryPageHandler"))
                                     .andReturn();

            //url 요청에서 전달받은 값
            Collection<OrderHistory> allOrderHistoryItem = (Collection<OrderHistory>) mvcResult.getModelAndView()
                                                                                               .getModel()
                                                                                               .get("orderHistory");

            //데이터베이스에서 전달받은 주문내역과
            //url 요청에서 전달받은 주문내역이 같은지 검증
            Assertions.assertEquals(allOrderHistory.size(), allOrderHistoryItem.size());

            Object[] itemToArray = allOrderHistory.toArray();
            Object[] otherItemToArray = allOrderHistoryItem.toArray();

            boolean allMatch = IntStream.range(0, itemToArray.length)
                                        .allMatch(idx -> itemToArray[idx].equals(otherItemToArray[idx]));

            assertTrue(allMatch);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


}


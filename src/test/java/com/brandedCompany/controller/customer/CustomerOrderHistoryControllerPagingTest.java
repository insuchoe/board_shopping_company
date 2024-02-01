package com.brandedCompany.controller.customer;

import com.brandedCompany.domain.Order;
import com.brandedCompany.domain.OrderHistory;
import com.brandedCompany.domain.OrderItem;
import com.brandedCompany.domain.handler.OrderHistoryPageHandler;
import com.brandedCompany.domain.searchCondition.OrderHistorySearchCondition;
import com.brandedCompany.serivce.OptionService;
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
import java.util.Objects;
import java.util.stream.IntStream;

import static com.brandedCompany.util.DomainUtils.TABLE.ORDERS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/root-context.xml"})
@Sql({"classpath:schema/brandedCompany_truncate.sql", "classpath:schema/brandedCompany_data.sql"})
public class CustomerOrderHistoryControllerPagingTest
{
    private MockMvc mvc;
    @Autowired private CustomerOrderHistoryController controller;
     @Autowired private OptionService service;


    @Before
    public void setUp()
    {
        InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
        internalResourceViewResolver.setPrefix("/WEB-INF/views/");
        internalResourceViewResolver.setSuffix(".jsp");
        mvc = MockMvcBuilders.standaloneSetup(controller)
                             .setViewResolvers(internalResourceViewResolver)
                             .build();
    }

    @DisplayName("49번 고객의 주문내역 2페이지 요청")
    @Test
    public void page2OfCustomerId49()
    {
        //given
        //주문자 아이디
        final BigInteger ORDERER_ID = BigInteger.valueOf(49);
        //페이지
        final Integer PAGE = 2;
        try
        {
            //when
            //49번고객 2페이지 주문내역 페이지 요청
            MvcResult mvcResult = mvc.perform(get("/customer/" + ORDERER_ID + "/orderHistory?page=" + PAGE))
                                     //then
                                     //페이지핸들러, 주문내역
                                     .andExpect(model().attributeExists("pageHandler", "items"))
                                     .andReturn();

            //페이징 아이템
            Object[] items = ((Collection<OrderHistory>) mvcResult.getRequest()
                                                                  .getAttribute("items")).toArray();

            //페이지핸들러
            OrderHistorySearchCondition searchCondition = getPageHandler(ORDERER_ID, PAGE).getSearchCondition();
            //데이터베이스의 49번고객 2페이지 주문내역
            Object[] orderHistory = service.pageAndSort(searchCondition)
                                           .toArray();

            //뷰페이지로 전달될 데이터와 실제 데이터베이스의 데이터 동일여부 결과
            boolean allMatching = IntStream.range(0, items.length)
                                           .allMatch(index -> items[index].equals(orderHistory[index]));
            //then
            //동일여부 결과 검사
            Assertions.assertTrue(allMatching);
        }
        catch (Exception e)
        {
            //   e.printStackTrace();
        }

    }

    @DisplayName("49번 고객의 최근1개월간 주문내역 1페이지 요청")
    @Test
    public void page1OfCustomerId49InLastMonthAgo()
    {
        //given
        //주문자 아이디
        final BigInteger ORDERER_ID = BigInteger.valueOf(49);
        //페이지
        final Integer PAGE = 1;
        final Integer MONTH_AGO = 1;
        try
        {
            BigInteger orderId = service.selectNextSequence(ORDERS);
            //테스트 할 주문 데이터 추가
            service.insert(new Order(orderId, ORDERER_ID, Order.OrderStatus.Pending));
            //테스트 할 주문 아이템 데이터 추가
            service.insert(new OrderItem(orderId, BigInteger.valueOf(228), BigInteger.ONE, 1.0, 3410.46));

            //when
            //49번고객 촤근1개월간 주문내역 페이지 요청
            MvcResult mvcResult = mvc.perform(get("/customer/" + ORDERER_ID + "/orderHistory?page=" + PAGE + "&monthAgo=" + MONTH_AGO))
                                     //then
                                     //페이지핸들러, 주문내역
                                     .andExpect(model().attributeExists("pageHandler", "items"))
                                     .andReturn();

            //페이징 아이템
            Object[] items = ((Collection<OrderHistory>) mvcResult.getRequest()
                                                                  .getAttribute("items")).toArray();

            //페이지핸들러
            OrderHistorySearchCondition searchCondition = getPageHandler(ORDERER_ID, PAGE, MONTH_AGO).getSearchCondition();
            //데이터베이스의 49번고객 최근1개월간 주문내역
            Object[] orderHistory = service.pageAndSort(searchCondition)
                                           .toArray();

            //뷰페이지로 전달될 데이터와 실제 데이터베이스의 데이터 동일여부 결과
            boolean allMatching = IntStream.range(0, items.length)
                                           .allMatch(index -> items[index].equals(orderHistory[index]));
            //then
            //동일여부 결과검사
            Assertions.assertTrue(allMatching);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @DisplayName("49번 고객의 최근3개월간 주문내역 1페이지 요청")
    @Test
    public void page1OfCustomerId49InLast3MonthAgo()
    {
        //given
        //주문자 아이디
        final BigInteger ORDERER_ID = BigInteger.valueOf(49);
        //페이지
        final Integer PAGE = 1;
        final Integer MONTH_AGO = 3;
        try
        {
            BigInteger orderId = service.selectNextSequence(ORDERS);
            //테스트 할 주문 데이터 추가
            service.insert(new Order(orderId, ORDERER_ID, Order.OrderStatus.Pending));
            //테스트 할 주문 아이템 데이터 추가
            service.insert(new OrderItem(orderId, BigInteger.valueOf(228), BigInteger.ONE, 1.0, 3410.46));

            //when
            //49번고객 최근 3개월간의 주문내역 1페이지 요청
            MvcResult mvcResult = mvc.perform(get("/customer/" + ORDERER_ID + "/orderHistory?page=" + PAGE + "&monthAgo=" + MONTH_AGO))
                                     //then
                                     //페이지핸들러, 주문내역
                                     .andExpect(model().attributeExists("pageHandler", "items"))
                                     .andReturn();

            //페이징
            Object[] items = ((Collection<OrderHistory>) mvcResult.getRequest()
                                                                  .getAttribute("items")).toArray();

            //페이지핸들러
            OrderHistorySearchCondition searchCondition = getPageHandler(ORDERER_ID, PAGE, MONTH_AGO).getSearchCondition();
            //데이터베이스의 49번고객 최근3개월간 주문내역
            Object[] orderHistory = service.pageAndSort(searchCondition)
                                           .toArray();

            //뷰페이지로 전달될 데이터와 실제 데이터베이스의 데이터와 동일여부 결과
            boolean allMatching = IntStream.range(0, items.length)
                                           .allMatch(index -> items[index].equals(orderHistory[index]));
            //then
            //동일여부 결과검사
            Assertions.assertTrue(allMatching);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @DisplayName("49번 고객의 최근6개월 주문내역 1페이지 요청")
    @Test
    public void page1OfCustomerId49InLast6MonthAgo()
    {
        //given
        //주문자 아이디
        final BigInteger ORDERER_ID = BigInteger.valueOf(49);
        //페이지
        final Integer PAGE = 1;
        final Integer MONTH_AGO = 6;
        try
        {
            BigInteger orderId = service.selectNextSequence(ORDERS);
            //테스트 할 주문 데이터 추가
            service.insert(new Order(orderId, ORDERER_ID, Order.OrderStatus.Pending));
            //테스트 할 주문 아이템 데이터 추가
            service.insert(new OrderItem(orderId, BigInteger.valueOf(228), BigInteger.ONE, 1.0, 3410.46));

            //when
            //49번고객 최근 6개월간의 주문내역 1페이지 요청
            MvcResult mvcResult = mvc.perform(get("/customer/" + ORDERER_ID + "/orderHistory?page=" + PAGE + "&monthAgo=" + MONTH_AGO))
                                     //then
                                     //페이지핸들러, 주문내역
                                     .andExpect(model().attributeExists("pageHandler", "items"))
                                     .andReturn();

            //페이징
            Object[] items = ((Collection<OrderHistory>) mvcResult.getRequest()
                                                                  .getAttribute("items")).toArray();

            //페이지핸들러
            OrderHistorySearchCondition searchCondition = getPageHandler(ORDERER_ID, PAGE, MONTH_AGO).getSearchCondition();
            //데이터베이스의 49번고객 최근 6개월간 주문내역 1페이지
            Object[] orderHistory = service.pageAndSort(searchCondition)
                                           .toArray();

            //뷰페이지로 전달될 데이터와 실제 데이터베이스의 데이터와 동일여부 결과
            boolean allMatching = IntStream.range(0, items.length)
                                           .allMatch(index -> items[index].equals(orderHistory[index]));
            //then
            //동일여부 결과검사
            Assertions.assertTrue(allMatching);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @DisplayName("49번 고객의 최근1개월 주문내역 페이지 요청_주문내역 없음")
    @Test
    public void page1OfCustomerId49InLastMonthAgo_NoOrderHistory()
    {
        //given
        //주문자 아이디
        final BigInteger ORDERER_ID = BigInteger.valueOf(49);
        //페이지
        final int PAGE = 2;
        final int MONTH_AGO = 1;
        try
        {
            //when
            //49번고객 1페이지 주문내역 페이지 요청
            MvcResult mvcResult = mvc.perform(get("/customer/" + ORDERER_ID + "/orderHistory?page=" + PAGE + "&monthAgo=" + MONTH_AGO))
                                     //then
                                     //페이지핸들러, 주문내역
                                     .andReturn();

            //데이터베이스의 주문내역
            Collection<OrderHistory> items = (Collection<OrderHistory>) mvcResult.getRequest()
                                                                                 .getAttribute("items");

            //then
            boolean allIsNull = items.stream()
                                     .allMatch(Objects::isNull);
            //주문내역 데이터가 없는지 검사
            Assertions.assertTrue(allIsNull);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private OrderHistoryPageHandler getPageHandler(BigInteger ordererId, Integer page, Integer monthAgo) throws ClassNotFoundException
    {
        OrderHistorySearchCondition.OrderHistorySearchConditionBuilder pageHandlerBuilder = new OrderHistorySearchCondition.OrderHistorySearchConditionBuilder();
        pageHandlerBuilder.customerId(ordererId);
        pageHandlerBuilder.monthAgo(monthAgo);
        pageHandlerBuilder.page(page);
        OrderHistorySearchCondition searchCondition = pageHandlerBuilder.build();
        return new OrderHistoryPageHandler(service.count(searchCondition), searchCondition);
    }

    private OrderHistoryPageHandler getPageHandler(BigInteger ordererId, Integer page) throws ClassNotFoundException
    {
        OrderHistorySearchCondition.OrderHistorySearchConditionBuilder pageHandlerBuilder = new OrderHistorySearchCondition.OrderHistorySearchConditionBuilder();
        pageHandlerBuilder.customerId(ordererId);
        pageHandlerBuilder.page(page);
        OrderHistorySearchCondition searchCondition = pageHandlerBuilder.build();
        return new OrderHistoryPageHandler(service.count(searchCondition), searchCondition);
    }
}
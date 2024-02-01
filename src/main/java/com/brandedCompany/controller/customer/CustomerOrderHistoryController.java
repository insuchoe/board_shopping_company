package com.brandedCompany.controller.customer;

import com.brandedCompany.controller.employee.AllCustomerOrderHistoryController;
import com.brandedCompany.domain.OrderHistory;
import com.brandedCompany.domain.handler.OrderHistoryPageHandler;
import com.brandedCompany.exception.DomainNotFoundException;
import com.brandedCompany.serivce.PagingAndSortingService;
import com.brandedCompany.util.CustomerControllerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// 고객 주문 내역 컨트롤러
@Controller
@RequestMapping("/customer/{customerId}/orderHistory")
public class CustomerOrderHistoryController {
    private final PagingAndSortingService service;
    private final CustomerControllerUtils util;
    private final Logger logger = LoggerFactory.getLogger(CustomerOrderHistoryController.class);


    @Autowired
    public CustomerOrderHistoryController(@Qualifier("pagingAndSortingServiceImpl") PagingAndSortingService service,
                                          CustomerControllerUtils util) {
        this.service = service;
        this.util= util;
    }

    // 주문 내역 보기
    @GetMapping
    public String goOrderHistoryPage(@PathVariable BigInteger customerId, HttpServletRequest request, Model model) throws Exception {
//        model.addAttribute("customerId", customerId);
        OrderHistoryPageHandler pageHandler = util.getOrderHistoryPageHandler(request,customerId);
        model.addAttribute("pageHandler", pageHandler);

        Collection<OrderHistory> orderHistory = (Collection<OrderHistory>) service.pageAndSort(pageHandler.getSearchCondition());
        model.addAttribute("items", orderHistory);
        Map<BigInteger, Double> orderIdTotalPrice = new HashMap<>();

        // 주문 내역 - 주문 금액
        orderHistory.forEach(oh ->
        {
            if (!orderIdTotalPrice.containsKey(oh.getOrderId()))
                orderIdTotalPrice.put(oh.getOrderId(), oh.getTotalPrice());
            else
                orderIdTotalPrice.put(oh.getOrderId(), orderIdTotalPrice.get(oh.getOrderId()) + oh.getTotalPrice());
        });

        model.addAttribute("orderIdTotalPrice", orderIdTotalPrice);

        // 테이블 RowSpan 에 사용 될 같은 주문내역의 수

        Map<BigInteger, Integer> idRowSpans = new HashMap<>();
        for (OrderHistory history : orderHistory) {
            if (idRowSpans.containsKey(history.getOrderId())) {
                idRowSpans.put(history.getOrderId(), idRowSpans.get(history.getOrderId()) + 1);
            } else
                idRowSpans.put(history.getOrderId(), 1);
        }
        model.addAttribute("idsRowSpans", idRowSpans);
        return "customerOrderHistory";

    }


}

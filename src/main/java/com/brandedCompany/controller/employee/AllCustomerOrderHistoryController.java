package com.brandedCompany.controller.employee;

import com.brandedCompany.domain.OrderHistory;
import com.brandedCompany.domain.handler.OrderHistoryPageHandler;
import com.brandedCompany.serivce.PagingAndSortingService;
import com.brandedCompany.util.EmployeeControllerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/employee/{employeeId}/orderHistory")
public class AllCustomerOrderHistoryController
{
    private final Logger logger = LoggerFactory.getLogger(AllCustomerOrderHistoryController.class);
    private final PagingAndSortingService service;
    private final EmployeeControllerUtils utils;

    @Autowired
    public AllCustomerOrderHistoryController(
        @Qualifier("pagingAndSortingServiceImpl")
            PagingAndSortingService service,
        EmployeeControllerUtils utils
        )
    {
        this.service = service;
        this.utils = utils;
    }


//    @ExceptionHandler({IllegalArgumentException.class,NumberFormatException.class})
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public String catchException3(Exception e)
//    {
//        logger.error("message:"+e.getMessage());
//        e.printStackTrace();
//        return "employeeRequestParameterError";
//    }
//
//    @ExceptionHandler({DomainNotFoundException.class, ClassNotFoundException.class,})
//    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
//    public String catchException(Exception e)
//    {
//        logger.error("message:" + e.getMessage());
//        e.printStackTrace();
//        return "employeeMyPage";
//    }

//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public String catchException2(Exception e)
//    {
//        logger.error("message:" + e.getMessage());
//        e.printStackTrace();
//        return "employeeMyPage";
//    }

    @GetMapping
    public String selectAllCustomerOrderHistory(
        @PathVariable
            BigInteger employeeId, HttpServletRequest request, Model model) throws Exception
    {

//        model.addAttribute("employeeId", employeeId);
        OrderHistoryPageHandler pageHandler = utils.getOrderHistoryPageHandler(request);
        model.addAttribute("orderHistoryPageHandler", pageHandler);

        Collection<OrderHistory> orderHistory = (Collection<OrderHistory>) service.pageAndSort(pageHandler.getSearchCondition());
        model.addAttribute("orderHistory", orderHistory);
        Map<BigInteger, Double> orderIdTotalPrice = new HashMap<>();

        orderHistory.stream()
                    .forEach(oh ->
                             {
                                 if (!orderIdTotalPrice.containsKey(oh.getOrderId()))
                                     orderIdTotalPrice.put(oh.getOrderId(), oh.getTotalPrice());
                                 else
                                     orderIdTotalPrice.put(oh.getOrderId(), orderIdTotalPrice.get(oh.getOrderId()) + oh.getTotalPrice());
                             });

        //        orderIdTotalPrice.forEach((bigInteger, aDouble) -> System.out.println(bigInteger + ", " + aDouble));
        model.addAttribute("orderIdTotalPrice", orderIdTotalPrice);

        // 테이블 RowSpan 에 사용 될 같은 주문내역의 수
        Map<BigInteger, Integer> orderHistoryRowSpan = new HashMap<>();

        Map<BigInteger, Integer> idRowSpans = new HashMap();
        for (OrderHistory history : orderHistory)
        {
            if (idRowSpans.containsKey(history.getOrderId()))
            {
                idRowSpans.put(history.getOrderId(), idRowSpans.get(history.getOrderId()) + 1);
            }
            else
                idRowSpans.put(history.getOrderId(), 1);
        }

        model.addAttribute("idsRowSpans", idRowSpans);
        logger.info("[" + employeeId + "] 주문내역");
        return "allCustomerOrderHistory";
    }



}

package com.brandedCompany.controller.customer;

import com.brandedCompany.domain.handler.ProductPageHandler;
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

@Controller
@RequestMapping("/customer/{customerId}/product")
public class CustomerProductController
{
    private static final Logger logger = LoggerFactory.getLogger(CustomerProductController.class);

    private final PagingAndSortingService service;
    private final CustomerControllerUtils util;

    @Autowired
    public CustomerProductController(
        @Qualifier("pagingAndSortingServiceImpl")
            PagingAndSortingService service, CustomerControllerUtils util)
    {
        this.service = service;
        this.util = util;
    }

    @GetMapping
    public String goToProductPage(
        @PathVariable
            BigInteger customerId, HttpServletRequest request, Model model) throws Exception
    {
//        model.addAttribute("customerId", customerId);
        ProductPageHandler pageHandler = util.getProductPageHandler(request);

        model.addAttribute("pageHandler", pageHandler);
        model.addAttribute("items", service.pageAndSort(pageHandler.getSearchCondition()));
        logger.info("["+customerId+"] go to product page");
        //        model.addAttribute("customer", service.select(CUSTOMERS, customerId));
        return "customerProduct";
    }


}

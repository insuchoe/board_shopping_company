package com.brandedCompany.controller.employee;

import com.brandedCompany.domain.Inventory;
import com.brandedCompany.domain.handler.InventoryPageHandler;
import com.brandedCompany.exception.DomainNotFoundException;
import com.brandedCompany.serivce.PagingAndSortingService;
import com.brandedCompany.util.EmployeeControllerUtils;
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

import static com.brandedCompany.util.DomainUtils.TABLE.PRODUCTS;

@Controller
@RequestMapping("/employee/{employeeId}")
public class EmployeeInventoryController
{
    private final Logger logger = LoggerFactory.getLogger(EmployeeInventoryController.class);
    private final PagingAndSortingService service;
    private final EmployeeControllerUtils util;


    @Autowired
    public EmployeeInventoryController(
        @Qualifier("pagingAndSortingServiceImpl")
            PagingAndSortingService service, EmployeeControllerUtils util)
    {
        this.service = service;
        this.util = util;
    }



    @GetMapping("/inventory")
    public String selectAllInventory(
        @PathVariable
            BigInteger employeeId, HttpServletRequest request, Model model) throws Exception
    {
//        model.addAttribute("employeeId", employeeId);
        InventoryPageHandler inventoryPageHandler = util.getInventoryPageHandler(request);

        model.addAttribute("inventoryPageHandler", inventoryPageHandler);
        model.addAttribute("inventoryDetails", service.pageAndSort(inventoryPageHandler.getSearchCondition()));
        return "employeeInventory";
    }




}

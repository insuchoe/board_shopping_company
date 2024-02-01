package com.brandedCompany.advice;

import com.brandedCompany.controller.employee.EmployeeLoginOutController;
import com.brandedCompany.exception.DomainNotFoundException;
import com.brandedCompany.exception.EmployeeNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice("com.brandedCompany.controller.employee")
@Order(Ordered.LOWEST_PRECEDENCE)
public class EmployeeControllerGlobalCatcher
{
    private static final Logger logger = LoggerFactory.getLogger(EmployeeControllerGlobalCatcher.class);
    private String view="";

    public String getView()
    {
        return view;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //400
    public String catchException(Exception e)
    {
        logger.error(e.getMessage());
        e.printStackTrace();
        view="employeeInternalServerError";
        return view;
    }
    @ExceptionHandler({IllegalArgumentException.class,NumberFormatException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String catchException2(Exception e,HttpServletRequest request,Model model)
    {
        logger.error(e.getMessage());
        e.printStackTrace();
        view="employeeRequestParameterError";
        return view;
    }
    @ExceptionHandler({DomainNotFoundException.class, ClassNotFoundException.class,})
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String catchException3( Exception e)
    {
        logger.error(e.getMessage());
        e.printStackTrace();
        view="employeeMyPage";
        return view;
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EmployeeNotFoundException.class)
    public String catchException4(EmployeeNotFoundException e, Model model)
    {
        logger.error(e.getMessage());
        e.printStackTrace();
        model.addAttribute("empLgnErr", e.getErrorCode());
        view="employeeLogin";
        return view;
    }
}

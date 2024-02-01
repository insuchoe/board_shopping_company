package com.brandedCompany.advice;

import com.brandedCompany.controller.customer.interceptor.CustomerControllerRequestParameterInterceptor;
import com.brandedCompany.exception.CustomerNotFoundException;
import com.brandedCompany.exception.DomainNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice(basePackages ="com.brandedCompany.controller.customer",
                  assignableTypes = CustomerControllerRequestParameterInterceptor.class)
@Order(Ordered.LOWEST_PRECEDENCE)
public class CustomerControllerGlobalCatcher
{
    private static final Logger logger = LoggerFactory.getLogger(CustomerControllerGlobalCatcher.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //400
    public String catchException(Exception e)
    {
        logger.error(e.getMessage());
        e.printStackTrace();
        return "customerInternalServerError";
    }
    @ExceptionHandler({IllegalArgumentException.class,NumberFormatException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String catchException2(Exception e)
    {
        logger.error(e.getMessage());
        e.printStackTrace();
        return "customerRequestParameterError";
    }
    @ExceptionHandler({DomainNotFoundException.class, ClassNotFoundException.class,})
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String catchException3(HttpServletRequest request, Exception e)
    {
        logger.error(e.getMessage());
        e.printStackTrace();
        return "customerMyPage";
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CustomerNotFoundException.class)
    public String catchException4(CustomerNotFoundException e, Model model)
    {

        logger.error(e.getMessage());
        e.printStackTrace();
        model.addAttribute("cusLgnErr", e.getErrorCode());
        return "customerLogin";
    }
}

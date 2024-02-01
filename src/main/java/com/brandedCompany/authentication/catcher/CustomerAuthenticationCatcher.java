package com.brandedCompany.authentication.catcher;

import com.brandedCompany.authentication.exception.*;
import com.brandedCompany.controller.customer.CustomerLoginOutController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice(
                  assignableTypes = {CustomerLoginOutController.class})
public class CustomerAuthenticationCatcher
{
    private final Logger logger = LoggerFactory.getLogger(CustomerAuthenticationCatcher.class);


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(CustomerAuthenticationNullPointerException.class)
    public String employeeAuthenticationNullPointerException(CustomerAuthenticationNullPointerException e)
    {
        String redirect = "redirect:/customer/error/internalServerError";
        logger.error("catch " + e.getClass().getName());
        logger.error("-> " + redirect);
        return redirect;

    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CustomerAuthenticationTargetNullPointerException.class)
    public String customerAuthenticationTargetNullPointerException(Exception e)
    {
        String redirect = "redirect:/customer/error/badRequest";
        logger.error("catch " + e.getClass().getName());
        logger.error("-> " + redirect);
        return redirect;

    }
}

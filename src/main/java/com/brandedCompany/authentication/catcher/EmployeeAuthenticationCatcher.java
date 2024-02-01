package com.brandedCompany.authentication.catcher;

import com.brandedCompany.authentication.exception.EmployeeAuthenticationNullPointerException;
import com.brandedCompany.authentication.exception.EmployeeAuthenticationTargetNullPointerException;
import com.brandedCompany.concurrentConnection.exception.ConcurrentConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice(
                  assignableTypes = {EmployeeAuthenticationTargetNullPointerException.class, EmployeeAuthenticationNullPointerException.class})
public class EmployeeAuthenticationCatcher
{
    private final Logger logger = LoggerFactory.getLogger(EmployeeAuthenticationCatcher.class);

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(EmployeeAuthenticationNullPointerException.class)
    public String employeeAuthenticationNullPointerException(EmployeeAuthenticationNullPointerException e)
    {
        String view = "employeeInternalServerErrorPage";
        logger.error("catch " + e.getClass()
                                 .getName());
        return view;

    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EmployeeAuthenticationTargetNullPointerException.class)
    public String employeeAuthenticationTargetNullPointerException(EmployeeAuthenticationTargetNullPointerException e)
    {
        String view = "employeeBadRequestPage";
        logger.error("catch " + e.getClass()
                                 .getName());
        return view;

    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ConcurrentConnectionException.class)
    public String employeeConcurrentConnectionException(ConcurrentConnectionException e)
    {
        String view = "employeeInternalServerErrorPage";
        logger.error("catch " + e.getClass()
                                 .getName());
        return view;
    }

    /*@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String exception(Exception e)
    {
        switch (e.getClass().getSimpleName())
        {
            case "EmployeeAuthenticationTargetNullPointerException":
                return employeeAuthenticationTargetNullPointerException((EmployeeAuthenticationTargetNullPointerException) e);
            case "EmployeeAuthenticationTargetPointerException":
                return employeeAuthenticationNullPointerException((EmployeeAuthenticationNullPointerException) e);
            default:
                String view = "employeeInternalServerErrorPage";
                logger.error("catch " + e.getClass().getName());
                return  view;
        }

    }*/


}

package com.brandedCompany.concurrentConnection.catcher;

import com.brandedCompany.authentication.exception.AuthenticationCastException;
import com.brandedCompany.authentication.exception.AuthenticationRuntimeException;
import com.brandedCompany.authentication.exception.CustomerAuthenticationNullPointerException;
import com.brandedCompany.concurrentConnection.exception.*;
import com.brandedCompany.controller.customer.CustomerLoginOutController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice(assignableTypes = CustomerLoginOutController.class)
public class CustomerConcurrentConnectionObserverCatcher
{
    private final Logger logger = LoggerFactory.getLogger(CustomerConcurrentConnectionObserverCatcher.class);

    final private String LOGIN_PAGE="customerLogin";
    @ResponseStatus(HttpStatus.LOCKED)
    @ExceptionHandler(ConcurrentConnectionRuntimeException.class)
    public String catchConcurrentConnectionRuntime(ConcurrentConnectionRuntimeException e,Model model)
    {

        //   e.printStackTrace();
        if(e instanceof   ConnectionCumulativeException)
            return catchConcurrentConnectionCumulative(e, model);
        else if(e instanceof ConnectionExceedException)
            return catchConcurrentConnectionExceed(e);
        return LOGIN_PAGE;
    }

    @ResponseStatus(HttpStatus.GONE)
    @ExceptionHandler(AuthenticationRuntimeException.class)
    public String catchAuthenticationRuntime(AuthenticationRuntimeException e)
    {
        //  e.printStackTrace();
        if(e instanceof CustomerAuthenticationNullPointerException)
             return catchCustomerAuthenticationNullPointerException( e);
        else if (e instanceof AuthenticationCastException)
             return catchAuthenticationCastException( e);
        return LOGIN_PAGE;
    }

    private String catchAuthenticationCastException(AuthenticationRuntimeException causeException)
    {
        AuthenticationCastException e = (AuthenticationCastException) causeException;
        logErrorMessage(e.getMessage());
        return LOGIN_PAGE;
    }

    private String catchCustomerAuthenticationNullPointerException(AuthenticationRuntimeException causeException)
    {
        CustomerAuthenticationNullPointerException e = (CustomerAuthenticationNullPointerException) causeException;
        logErrorMessage(e.getMessage());
        return "customerLogin";
    }

    // 동시 접속

    private String catchConcurrentConnectionCumulative(ConcurrentConnectionRuntimeException e, Model model)
    {
        //   e.printStackTrace();
        logErrorMessage(e.getMessage());
        model.addAttribute("cusLgnErr", "customer.concurrentConnection");
        return LOGIN_PAGE;
    }

    // 동시 접속 시도 횟수 초과

    private String catchConcurrentConnectionExceed(ConcurrentConnectionRuntimeException e)
    {
        e.printStackTrace();
        logErrorMessage(e.getMessage());
        return "concurrentConnectionExceed";
    }

    private void logErrorMessage(String message)
    {
        logger.error(message);
    }

}

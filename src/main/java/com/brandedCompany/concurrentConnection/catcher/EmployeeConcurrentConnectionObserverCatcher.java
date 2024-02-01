package com.brandedCompany.concurrentConnection.catcher;

import com.brandedCompany.authentication.exception.AuthenticationCastException;
import com.brandedCompany.authentication.exception.AuthenticationRuntimeException;
import com.brandedCompany.authentication.exception.EmployeeAuthenticationNullPointerException;
import com.brandedCompany.concurrentConnection.exception.ConcurrentConnectionRuntimeException;
import com.brandedCompany.concurrentConnection.exception.ConnectionExceedException;
import com.brandedCompany.controller.employee.EmployeeLoginOutController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice(assignableTypes = EmployeeLoginOutController.class)
public class EmployeeConcurrentConnectionObserverCatcher
{
    private final Logger logger = LoggerFactory.getLogger(EmployeeConcurrentConnectionObserverCatcher.class);

    final private String LOGIN_PAGE="employeeLogin";
    @ResponseStatus(HttpStatus.LOCKED)
    @ExceptionHandler(ConcurrentConnectionRuntimeException.class)
    public String catchConcurrentConnectionRuntime(ConcurrentConnectionRuntimeException e)
    {
        if(e instanceof ConnectionExceedException)
             return catchConcurrentConnectionExceed(e);
        return LOGIN_PAGE;
    }

    @ResponseStatus(HttpStatus.GONE)
    @ExceptionHandler(AuthenticationRuntimeException.class)
    public String catchAuthenticationRuntime(AuthenticationRuntimeException e)
    {
        //   e.printStackTrace();
        if(e instanceof EmployeeAuthenticationNullPointerException)
             return catchEmployeeAuthenticationNullPointerException(e);
        else if (e instanceof AuthenticationCastException)
             return catchAuthenticationCastException( e);
        return LOGIN_PAGE;
    }

    private String catchAuthenticationCastException(AuthenticationRuntimeException causeException)
    {
        AuthenticationCastException e = (AuthenticationCastException) causeException;
        logMessage(e.getMessage());
        return LOGIN_PAGE;
    }

    private String catchEmployeeAuthenticationNullPointerException(AuthenticationRuntimeException e)
    {
        //  e.printStackTrace();
        EmployeeAuthenticationNullPointerException e2 = (EmployeeAuthenticationNullPointerException) e;
        logMessage(e2.getMessage());
        return "employeeLogin";
    }

    // 동시 접속 시도 횟수 초과
    private String catchConcurrentConnectionExceed(ConcurrentConnectionRuntimeException e)
    {
        //   e.printStackTrace();
        logMessage(e.getMessage());
        return "concurrentConnectionExceed";
    }

    private void logMessage(String message)
    {
        logger.error(message);
    }

}

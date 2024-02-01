package com.brandedCompany.advice;

import com.brandedCompany.advice.util.CommonExceptionAdviceUtil;
import com.brandedCompany.domain.Customer;
import com.brandedCompany.domain.Employee;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigInteger;
import java.util.regex.Pattern;

@ControllerAdvice
//@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ControllerCommonGlobalCatcher
{
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IndexOutOfBoundsException.class, NumberFormatException.class})
    public String catchGettingConnectorIdError(Exception e,HttpServletRequest request)
    {
        e.printStackTrace();
        String path = request.getRequestURI();
        CommonExceptionAdviceUtil util = new CommonExceptionAdviceUtil();

        String type=containsType(path);

        if (!util.isMatching(path, type))
        {
            if("employee".equals(type))
            {
                return "employeeNotFoundPage";
            }
            else if("customer".equals(type))
            {
                return "customerNotFoundPage";
            }
        }

        return "notFoundPage";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String notFound(HttpServletRequest request ,Exception e) throws IOException
    {
        e.printStackTrace();
        String path = request.getRequestURI();
        CommonExceptionAdviceUtil util = new CommonExceptionAdviceUtil();

        String type=containsType(path);

        if (!util.isMatching(path, type))
        {
            if("employee".equals(type))
            {
                return "employeeNotFoundPage";
            }
            else if("customer".equals(type))
            {
                return "customerNotFoundPage";
            }
        }

        return "notFoundPage";
    }

    private String containsType(String requestURI)
    {
        if (requestURI.contains("employee"))
            return  "employee";
        else if(requestURI.contains("customer"))
            return  "customer";
        return "";
    }
}

package com.brandedCompany.advice.util;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigInteger;
import java.util.regex.Pattern;

public class CommonExceptionAdviceUtil
{

    public BigInteger getConnectorId(String path)
    {

        int beginIndex;
        int endIndex;
        String id;

        {
            if (isMatching(path,"employee"))
            {
                beginIndex = path.indexOf("/", "/brandedCompany/employee/".length() - 1);
                endIndex = path.indexOf("/", beginIndex + 1);
                id = path.substring(beginIndex + 1, endIndex);
                return BigInteger.valueOf(Long.parseLong(id));
            }
            else if (isMatching(path,"customer"))
            {
                beginIndex = path.indexOf("/", "/brandedCompany/customer/".length() -1  );
                endIndex = path.indexOf("/", beginIndex + 1);
                id = path.substring(beginIndex + 1, endIndex);
                return BigInteger.valueOf(Long.parseLong(id));
            }
        }
        return null;

    }

    public boolean isMatching(String path,String type)
    {
        if (!(type.equals("employee")||type.equals("customer")))
            return false;
        switch (type)
        {
            case "customer":
            return Pattern.matches("^/brandedCompany/customer/[0-9]+/myPage$", path)||
                Pattern.matches("^/brandedCompany/customer/[0-9]+/product?\\S*$",path)||
                Pattern.matches("^/brandedCompany/customer/[0-9]+/product$",path)||
                Pattern.matches("^/brandedCompany/customer/[0-9]+/orderHistory$",path) ||
                Pattern.matches("^/brandedCompany/customer/[0-9]+/orderHistory?\\S*$",path)||
                Pattern.matches("^/brandedCompany/customer/[0-9]+/cart$", path)||
                Pattern.matches("^/brandedCompany/customer/[0-9]+/cart?\\S*$",path);
            case "employee":
             return Pattern.matches("^/brandedCompany/employee/[0-9]+/board$",path)||
                    Pattern.matches("^/brandedCompany/employee/[0-9]+/board?\\S*$",path)||
                    Pattern.matches("^/brandedCompany/employee/[0-9]+/board/new$",path)||
                    Pattern.matches("^/brandedCompany/employee/[0-9]+/board/new?\\S*$",path)||
                    Pattern.matches("^/brandedCompany/employee/[0-9]+/inventory$",path)||
                    Pattern.matches("^/brandedCompany/employee/[0-9]+/inventory?\\S*$",path)||
                    Pattern.matches("^/brandedCompany/employee/[0-9]+/orderHistory$",path)||
                    Pattern.matches("^/brandedCompany/employee/[0-9]+/orderHistory?\\S*$",path);
        }
        return false;
    }

}

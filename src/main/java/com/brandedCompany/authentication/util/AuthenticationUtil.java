package com.brandedCompany.authentication.util;

import com.brandedCompany.authentication.*;
import com.brandedCompany.authentication.exception.AuthenticationNullPointerException;
import com.brandedCompany.authentication.exception.CustomerAuthenticationNotFoundException;
import com.brandedCompany.authentication.exception.EmployeeAuthenticationNotFoundException;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Optional;

@Component
@NoArgsConstructor
public class AuthenticationUtil
{
    private final Logger logger = LoggerFactory.getLogger(AuthenticationUtil.class);
    private  CustomerAuthenticationManager customerAuthManager;
    private  EmployeeAuthenticationManager employeeAuthManager;

    @Autowired
    public void setCustomerAuthManager(CustomerAuthenticationManager customerAuthManager)
    {
        this.customerAuthManager = customerAuthManager;
    }
    @Autowired
    public void setEmployeeAuthManager(EmployeeAuthenticationManager employeeAuthManager)
    {
        this.employeeAuthManager = employeeAuthManager;
    }

    public BigInteger getConnectorId(String path)
    {
        int beginIndex;
        int endIndex;
        String id;
        switch (extractDomainType(path))
        {
            case EMPLOYEE:
                beginIndex = path.indexOf("/", "/brandedCompany/employee/".length()-1);
                endIndex = path.indexOf("/", beginIndex + 1);
                id = path.substring(beginIndex + 1, endIndex);
                return BigInteger.valueOf(Long.parseLong(id));

            case CUSTOMER:
                beginIndex = path.indexOf("/", "/brandedCompany/customer/".length()-1);
                endIndex = path.indexOf("/", beginIndex + 1);
                id = path.substring(beginIndex + 1, endIndex);
                return BigInteger.valueOf(Long.parseLong(id));
        }
        return null;

    }

    private DomainType extractDomainType(String requestURI)
    {
        if (0 == requestURI.indexOf("/brandedCompany/employee/"))
            return DomainType.EMPLOYEE;
        if (0 == requestURI.indexOf("/brandedCompany/customer/"))
            return DomainType.CUSTOMER;
        return null;
    }

    public boolean isValidAuthentication(Authentication authentication) throws AuthenticationNullPointerException
    {
        if(Optional.ofNullable(authentication).isEmpty()) throw new AuthenticationNullPointerException();

        switch (extractType(authentication))
        {
            case EMPLOYEE:
                return AuthenticationStatus.SUCCESS == ((EmployeeAuthentication) authentication).getStatus();
            case CUSTOMER:
                return AuthenticationStatus.SUCCESS == ((CustomerAuthentication) authentication).getStatus();
        }
        return false;
    }



    public void checkEmployeeAuthentication(EmployeeAuthentication authentication) throws EmployeeAuthenticationNotFoundException
    {
        if(!employeeAuthManager.hasAuthentication(authentication))
            throw new EmployeeAuthenticationNotFoundException("No authentication found.");
    }
    public void checkCustomerAuthentication(CustomerAuthentication authentication) throws CustomerAuthenticationNotFoundException
    {
        if(!customerAuthManager.hasAuthentication(authentication)) throw new CustomerAuthenticationNotFoundException("No authentication found.");
    }


    public void logAuthenticationStatus(Authentication authentication)
    {
        AuthenticationStatus status = null;
        switch (extractType(authentication))
        {
            case EMPLOYEE:
                status = ((EmployeeAuthentication) authentication).getStatus();

                if (AuthenticationStatus.SUCCESS == status)
                    logger.info("authentication " + AuthenticationStatus.SUCCESS);
                else
                {
                    if (AuthenticationStatus.FAILURE == status)
                        logger.error("authentication " + AuthenticationStatus.FAILURE);
                    else
                        logger.warn("authentication " + status);
                }
                break;
            case CUSTOMER:
                status = ((CustomerAuthentication) authentication).getStatus();

                if (AuthenticationStatus.SUCCESS == status)
                    logger.info("authentication " + AuthenticationStatus.SUCCESS);
                else
                {
                    if (AuthenticationStatus.FAILURE == status)
                        logger.error("authentication " + AuthenticationStatus.FAILURE);
                    else
                        logger.warn("authentication " + status);
                }
                break;
        }

    }

    private enum DomainType
    {
        EMPLOYEE,
        CUSTOMER;
    }

    private DomainType extractType(Authentication authentication)
    {
        if (authentication instanceof EmployeeAuthentication)
        {
            return DomainType.EMPLOYEE;
        }
        else if (authentication instanceof CustomerAuthentication)
        {
            return DomainType.CUSTOMER;
        }
        return null;
    }
}

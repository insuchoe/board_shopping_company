package com.brandedCompany.authentication;

import com.brandedCompany.authentication.exception.AuthenticationRuntimeException;
import com.brandedCompany.authentication.exception.CustomerAuthenticationFailException;
import com.brandedCompany.authentication.exception.CustomerAuthenticationTargetNullPointerException;
import com.brandedCompany.observer.Observer;
import com.brandedCompany.domain.Customer;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static com.brandedCompany.authentication.AuthenticationStatus.*;


@Getter
public class CustomerAuthentication implements Authentication, Observer
{
    final Logger logger = LoggerFactory.getLogger(CustomerAuthentication.class);
    private AuthenticationStatus status;
    private Customer target;

    public CustomerAuthentication(Customer target) throws CustomerAuthenticationTargetNullPointerException
    {
        if(Optional.ofNullable(target).isEmpty())
            throw new CustomerAuthenticationTargetNullPointerException();
        this.target = target;
        status = PREPARING;
    }

    @Override
    public void update(Object object) throws AuthenticationRuntimeException
    {
        status = PROCEEDING;
        if (Optional.ofNullable(object)
                    .isPresent())
            if (target.equals(object))
                status = SUCCESS;
            else
            {
                status = FAILURE;
                throw new CustomerAuthenticationFailException();
            }
        else
        {
            status = PREPARING;
            throw new CustomerAuthenticationTargetNullPointerException();
        }
    }

    @Override
    public void invalidate()
    {
        this.status = PREPARING;
        this.target = null;
    }


    @Override
    public String toString()
    {
        return "CustomerAuthentication{" + "status=" + status + ", target=" + target + '}';
    }
}

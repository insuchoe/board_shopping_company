package com.brandedCompany.authentication;


import com.brandedCompany.authentication.exception.AuthenticationRuntimeException;
import com.brandedCompany.authentication.exception.EmployeeAuthenticationFailException;
import com.brandedCompany.authentication.exception.EmployeeAuthenticationTargetNullPointerException;
import com.brandedCompany.observer.Observer;
import com.brandedCompany.domain.Employee;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static com.brandedCompany.authentication.AuthenticationStatus.*;

@Getter
public class EmployeeAuthentication implements Authentication, Observer
{
    final Logger logger = LoggerFactory.getLogger(EmployeeAuthentication.class);
    private AuthenticationStatus status;
    private Employee target;
    public EmployeeAuthentication(Employee target) throws EmployeeAuthenticationTargetNullPointerException
    {
        if(Optional.ofNullable(target).isEmpty())
        {
            status = PREPARING;
            throw new EmployeeAuthenticationTargetNullPointerException();
        }
        this.target = target;
        status = PROCEEDING;
    }

    @Override
    public void update(Object updateTarget) throws AuthenticationRuntimeException
    {
        if (Optional.ofNullable(updateTarget)
                    .isPresent())
            if (target.equals(updateTarget))
                status = SUCCESS;
            else
            {
                status = FAILURE;
                throw new EmployeeAuthenticationFailException();
            }
        else
        {
            status = PREPARING;
            throw new EmployeeAuthenticationTargetNullPointerException();
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
        return "EmployeeAuthentication{" + "status=" + status + ", target=" + target + '}';
    }
}

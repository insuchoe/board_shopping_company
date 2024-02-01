package com.brandedCompany.authentication.exception;

import com.brandedCompany.authentication.interceptor.EmployeeLoginAuthenticationInterceptor;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public class EmployeeAuthenticationTargetNullPointerException extends AuthenticationRuntimeException {

    private static final long serialVersionUID = 8925531533520096762L;
    final Logger logger = LoggerFactory.getLogger(EmployeeLoginAuthenticationInterceptor.class);

    public EmployeeAuthenticationTargetNullPointerException()
    {
    }

    public EmployeeAuthenticationTargetNullPointerException(String s)
    {
        super(s);
    }
}

package com.brandedCompany.authentication.exception;

import com.brandedCompany.authentication.CustomerAuthentication;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor
public class CustomerAuthenticationTargetNullPointerException extends AuthenticationRuntimeException
{
    private static final long serialVersionUID = 1781107297902895532L;
    String message;


    public CustomerAuthenticationTargetNullPointerException(String message)
    {
        this.message = message;
    }

    public CustomerAuthenticationTargetNullPointerException(String s, String message)
    {
        super(s);
        this.message = message;
    }
}

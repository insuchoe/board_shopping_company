package com.brandedCompany.authentication.exception;

import com.brandedCompany.authentication.Authentication;

import java.io.InvalidClassException;

public class AuthenticationInvalidException extends AuthenticationRuntimeException
{

    private static final long serialVersionUID = -8871876016632437717L;

    public AuthenticationInvalidException(String message)
    {
        super(message);
    }

    public AuthenticationInvalidException()
    {
    }
}

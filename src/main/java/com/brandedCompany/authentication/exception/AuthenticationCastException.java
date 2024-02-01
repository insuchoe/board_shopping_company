package com.brandedCompany.authentication.exception;

public class AuthenticationCastException extends AuthenticationRuntimeException
{
    private static final long serialVersionUID = 3807724130907331058L;

    public AuthenticationCastException()
    {
    }

    public AuthenticationCastException(String s)
    {
        super(s);
    }

    public AuthenticationCastException(String s, Throwable cause)
    {
        super(s, cause);
    }
}

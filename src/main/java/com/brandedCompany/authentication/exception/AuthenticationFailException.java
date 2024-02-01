package com.brandedCompany.authentication.exception;

public class AuthenticationFailException extends AuthenticationRuntimeException
{
    private static final long serialVersionUID = 1997314231355465139L;

    public AuthenticationFailException()
    {
    }

    public AuthenticationFailException(String s)
    {
        super(s);
    }

    public AuthenticationFailException(String s, Throwable cause)
    {
        super(s, cause);
    }

    public AuthenticationFailException(Throwable cause)
    {
        super(cause);
    }
}

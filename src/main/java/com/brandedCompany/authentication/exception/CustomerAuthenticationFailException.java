package com.brandedCompany.authentication.exception;

public class CustomerAuthenticationFailException extends AuthenticationFailException
{
    private static final long serialVersionUID = 1084528490222283997L;

    public CustomerAuthenticationFailException()
    {
    }

    public CustomerAuthenticationFailException(String s)
    {
        super(s);
    }

    public CustomerAuthenticationFailException(String s, Throwable cause)
    {
        super(s, cause);
    }

    public CustomerAuthenticationFailException(Throwable cause)
    {
        super(cause);
    }
}

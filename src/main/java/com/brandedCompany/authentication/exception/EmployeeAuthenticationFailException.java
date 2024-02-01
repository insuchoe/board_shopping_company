package com.brandedCompany.authentication.exception;

public class EmployeeAuthenticationFailException extends AuthenticationFailException
{
    private static final long serialVersionUID = 1084528490222283997L;

    public EmployeeAuthenticationFailException()
    {
    }

    public EmployeeAuthenticationFailException(String s)
    {
        super(s);
    }

    public EmployeeAuthenticationFailException(String s, Throwable cause)
    {
        super(s, cause);
    }

    public EmployeeAuthenticationFailException(Throwable cause)
    {
        super(cause);
    }
}

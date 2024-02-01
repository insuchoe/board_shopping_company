package com.brandedCompany.authentication.exception;

public class EmployeeAuthenticationNotFoundException extends AuthenticationNotFoundException
{

    private static final long serialVersionUID = 5886233658231696968L;

    public EmployeeAuthenticationNotFoundException()
    {
    }

    public EmployeeAuthenticationNotFoundException(String message)
    {
        super(message);
    }
}

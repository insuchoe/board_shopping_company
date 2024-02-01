package com.brandedCompany.authentication.exception;

public class AuthenticationNotFoundException extends AuthenticationRuntimeException
{

    private static final long serialVersionUID = -8091540206176992544L;

    public AuthenticationNotFoundException()
    {
    }

    public AuthenticationNotFoundException(String s)
    {
        super(s);
    }
}

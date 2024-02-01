package com.brandedCompany.authentication.exception;

public class AuthenticationRuntimeException extends AuthenticationException
{

    private static final long serialVersionUID = -3495791073192594009L;



    public AuthenticationRuntimeException() {
        super();
    }

    public AuthenticationRuntimeException(String s) {
        super(s);
    }

    public AuthenticationRuntimeException(String s, Throwable cause)
    {
        super(s,cause);
    }
    public AuthenticationRuntimeException(Throwable cause)
    {
        super(cause);
    }


}

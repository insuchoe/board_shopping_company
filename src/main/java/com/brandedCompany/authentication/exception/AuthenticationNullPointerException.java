package com.brandedCompany.authentication.exception;

public class AuthenticationNullPointerException extends AuthenticationRuntimeException
{
    private static final long serialVersionUID = 7546829917727757233L;

    public AuthenticationNullPointerException() {
        super();
    }

    public AuthenticationNullPointerException(String s) {
        super(s);
    }
}

package com.brandedCompany.concurrentConnection.exception;

public class ConcurrentConnectionNullPointerException extends ConcurrentConnectionRuntimeException
{

    private static final long serialVersionUID = -6127789517122958136L;

    public ConcurrentConnectionNullPointerException() {
        super();
    }

    public ConcurrentConnectionNullPointerException(String s) {
        super(s);
    }
}

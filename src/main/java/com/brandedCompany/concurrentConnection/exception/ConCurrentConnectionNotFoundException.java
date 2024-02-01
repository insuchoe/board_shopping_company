package com.brandedCompany.concurrentConnection.exception;

public class ConCurrentConnectionNotFoundException extends ConcurrentConnectionRuntimeException
{
    private static final long serialVersionUID = -8810925903529714374L;

    public ConCurrentConnectionNotFoundException(String msg) {
        super(msg);
    }

    public ConCurrentConnectionNotFoundException(String msg, Exception e) {
        super(msg + " because of " + e.toString());
    }
}

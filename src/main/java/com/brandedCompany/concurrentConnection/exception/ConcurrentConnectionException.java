package com.brandedCompany.concurrentConnection.exception;

public class ConcurrentConnectionException extends Throwable
{

    private static final long serialVersionUID = -8525413717471825886L;

    public ConcurrentConnectionException() {
        super();
    }

    public ConcurrentConnectionException(String message) {
        super(message);
    }

    public ConcurrentConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConcurrentConnectionException(Throwable cause) {
        super(cause);
    }

    protected ConcurrentConnectionException(String message, Throwable cause,
                                      boolean enableSuppression,
                                      boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

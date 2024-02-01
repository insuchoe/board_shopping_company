package com.brandedCompany.concurrentConnection.exception;

public class ConcurrentConnectionRuntimeException extends ConcurrentConnectionException
{
    private static final long serialVersionUID = 7132099721984926375L;



    public ConcurrentConnectionRuntimeException() {
        super();
    }

    public ConcurrentConnectionRuntimeException(String s) {
        super(s);
    }

    public ConcurrentConnectionRuntimeException(String s, Throwable cause)
    {
        super(s,cause);
    }

    public ConcurrentConnectionRuntimeException(Throwable cause)
    {
        super(cause);
    }


}

package com.brandedCompany.concurrentConnection.exception;

public class ConnectionCumulativeException extends ConcurrentConnectionRuntimeException
{
    private static final long serialVersionUID = -20659489155357825L;

    public ConnectionCumulativeException()
    {
    }

    public ConnectionCumulativeException(String s)
    {
        super(s);
    }

    public ConnectionCumulativeException(String s, Throwable cause)
    {
        super(s, cause);
    }

    public ConnectionCumulativeException(Throwable cause)
    {
        super(cause);
    }
}

package com.brandedCompany.concurrentConnection.exception;

public class ConnectionExceedException extends ConcurrentConnectionRuntimeException
{
    private static final long serialVersionUID = 4999744624116315316L;

    public ConnectionExceedException()
    {
    }

    public ConnectionExceedException(String s)
    {
        super(s);
    }

    public ConnectionExceedException(String s, Throwable cause)
    {
        super(s, cause);
    }

    public ConnectionExceedException(Throwable cause)
    {
        super(cause);
    }
}

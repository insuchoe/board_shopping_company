package com.brandedCompany.concurrentConnection.exception;

public class ConcurrentConnectionCumulativeException extends ConnectionCumulativeException
{
    private static final long serialVersionUID = 4968511590758137491L;

    public ConcurrentConnectionCumulativeException()
    {
    }

    public ConcurrentConnectionCumulativeException(String s)
    {
        super(s);
    }
}

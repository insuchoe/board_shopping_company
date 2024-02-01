package com.brandedCompany.concurrentConnection.exception;

import lombok.NoArgsConstructor;


public class ConcurrentConnectionExceedException extends ConnectionExceedException
{

    private static final long serialVersionUID = -2710658315564764045L;

    public ConcurrentConnectionExceedException()
    {
    }

    public ConcurrentConnectionExceedException(String s)
    {
        super(s);
    }
}

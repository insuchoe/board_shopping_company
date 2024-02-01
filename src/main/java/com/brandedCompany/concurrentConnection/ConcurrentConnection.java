package com.brandedCompany.concurrentConnection;

import com.brandedCompany.authentication.Authentication;

public abstract class ConcurrentConnection
{
    protected Integer count=0;
    protected Authentication authentication;
    protected ConcurrentConnectionStatus status;

    public void changeStatus(ConcurrentConnectionStatus status)
    {
        this.status = status;
    }
    public ConcurrentConnectionStatus getStatus()
    {
        return status;
    }
    public Integer getCount()
    {
        return count;
    }

    public Authentication getAuthentication()
    {
        return authentication;
    }

   abstract void incrementCount();

}

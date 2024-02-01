package com.brandedCompany.observer;

import com.brandedCompany.authentication.exception.AuthenticationRuntimeException;
import com.brandedCompany.concurrentConnection.exception.ConcurrentConnectionRuntimeException;

public interface Observer
{
    public void update(Object object) throws AuthenticationRuntimeException,  ConcurrentConnectionRuntimeException;

}

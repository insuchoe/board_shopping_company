package com.brandedCompany.concurrentConnection;

import com.brandedCompany.authentication.CustomerAuthentication;
import com.brandedCompany.authentication.exception.AuthenticationRuntimeException;
import com.brandedCompany.authentication.exception.CustomerAuthenticationNullPointerException;
import com.brandedCompany.concurrentConnection.exception.ConcurrentConnectionRuntimeException;
import com.brandedCompany.observer.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerConcurrentConnectionObserver implements Observer
{
    final Logger logger = LoggerFactory.getLogger(CustomerConcurrentConnectionObserver.class);
    private final CustomerConcurrentConnectionManager manager;

    @Autowired
    public CustomerConcurrentConnectionObserver(CustomerConcurrentConnectionManager manager)
    {
        this.manager = manager;
    }

    @Override
    public void update(Object object) throws AuthenticationRuntimeException, ConcurrentConnectionRuntimeException
    {
        if (null==object)
            throw new CustomerAuthenticationNullPointerException("parameter is empty.");

        CustomerAuthentication authentication = (CustomerAuthentication) object;

        CustomerConcurrentConnection concurrentConnection=null;
        if(manager.has(authentication))
            concurrentConnection = (CustomerConcurrentConnection) manager.get(authentication);
        else
         concurrentConnection = new CustomerConcurrentConnection( authentication, ConcurrentConnectionStatus.CUMULATIVE);
        concurrentConnection.incrementCount();
        manager.save(authentication, concurrentConnection);
        manager.refresh(authentication);
        manager.check(authentication);
    }
}

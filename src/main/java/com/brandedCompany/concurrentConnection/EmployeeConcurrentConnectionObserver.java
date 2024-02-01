package com.brandedCompany.concurrentConnection;

import com.brandedCompany.authentication.EmployeeAuthentication;
import com.brandedCompany.authentication.exception.*;
import com.brandedCompany.concurrentConnection.exception.ConcurrentConnectionRuntimeException;
import com.brandedCompany.observer.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmployeeConcurrentConnectionObserver implements Observer
{
    final Logger logger = LoggerFactory.getLogger(EmployeeConcurrentConnectionObserver.class);
    private final EmployeeConcurrentConnectionManager manager;

    @Autowired
    public EmployeeConcurrentConnectionObserver(EmployeeConcurrentConnectionManager manager)
    {
        this.manager = manager;
    }

    @Override
    public void update(Object object) throws AuthenticationRuntimeException, ConcurrentConnectionRuntimeException
    {
        if (null==object)
            throw new EmployeeAuthenticationNullPointerException("parameter is empty.");

            EmployeeAuthentication authentication = (EmployeeAuthentication) object;

            EmployeeConcurrentConnection concurrentConnection = null;
            if (manager.has(authentication))
                concurrentConnection = (EmployeeConcurrentConnection) manager.get(authentication);
            else
                concurrentConnection = new EmployeeConcurrentConnection(authentication);
            concurrentConnection.incrementCount();
            manager.save(authentication, concurrentConnection);
            manager.refresh(authentication);
            manager.check(authentication);
    }
}

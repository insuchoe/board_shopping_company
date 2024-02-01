package com.brandedCompany.concurrentConnection;

import com.brandedCompany.authentication.Authentication;
import com.brandedCompany.authentication.EmployeeAuthentication;
import com.brandedCompany.concurrentConnection.exception.ConcurrentConnectionExceedException;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
// 고객 동시 접속
public class EmployeeConcurrentConnection extends ConcurrentConnection
{
    final Logger logger = LoggerFactory.getLogger(EmployeeConcurrentConnection.class);
    public EmployeeConcurrentConnection(Authentication authentication)
    {
        this.authentication = authentication;
    }
    public EmployeeConcurrentConnection(EmployeeAuthentication authentication, ConcurrentConnectionStatus status) throws ConcurrentConnectionExceedException
    {
        this.authentication = authentication;
        this.status = status;
    }
    public void incrementCount()
    {
            this.count += 1;
            logger.info("increase concurrent connection count + 1 ");

    }
}

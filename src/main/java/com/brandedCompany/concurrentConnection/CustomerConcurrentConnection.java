package com.brandedCompany.concurrentConnection;

import com.brandedCompany.authentication.Authentication;
import com.brandedCompany.authentication.CustomerAuthentication;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
// 고객 동시 접속
public class CustomerConcurrentConnection extends ConcurrentConnection
{
    final Logger logger = LoggerFactory.getLogger(CustomerConcurrentConnection.class);

    //    public CustomerConcurrentConnection(Integer count,
    //    CustomerAuthentication authentication,ConcurrentConnectionStatus status)
    //    {
    //        this.count = count;
    //        this.authentication=authentication;
    //        this.status = status;
    //    }
    public CustomerConcurrentConnection(Authentication authentication)
    {
        this.authentication = authentication;
    }

    public CustomerConcurrentConnection(CustomerAuthentication authentication, ConcurrentConnectionStatus status)
    {
        this.authentication = authentication;
        if (ConcurrentConnectionStatus.CUMULATIVE == status || ConcurrentConnectionStatus.EXCEED == status)
        this.status = status;
    }

    @Override
    public void incrementCount()
    {
        autoIncrement();
    }

    private void autoIncrement()
    {
        this.count += 1;
        logger.info("increase concurrent connection count + 1 ");
    }


    //    public void changeStatus(ConcurrentConnectionStatus status)
    //    {
    //        this.status = status;
    //    }

    //    public Integer getCount()
    //    {
    //        return count;
    //    }
}

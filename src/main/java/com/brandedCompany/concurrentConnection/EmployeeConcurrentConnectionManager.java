package com.brandedCompany.concurrentConnection;

import com.brandedCompany.authentication.Authentication;
import com.brandedCompany.authentication.EmployeeAuthentication;
import com.brandedCompany.authentication.exception.AuthenticationNullPointerException;
import com.brandedCompany.authentication.exception.AuthenticationRuntimeException;
import com.brandedCompany.authentication.util.AuthenticationUtil;
import com.brandedCompany.concurrentConnection.exception.ConCurrentConnectionNotFoundException;
import com.brandedCompany.concurrentConnection.exception.ConcurrentConnectionRuntimeException;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLFeatureNotSupportedException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class EmployeeConcurrentConnectionManager extends ConcurrentConnectionManager
{
    private final ConcurrentConnectionLevel SAFE=ConcurrentConnectionLevel.SAFE;
    private final ConcurrentConnectionLevel UNSAFE=ConcurrentConnectionLevel.UNSAFE;

    //    private final Map<CustomerAuthentication, CustomerConcurrentConnection> REPOSITORY = new HashMap<>();

    @Getter
    private enum ConcurrentConnectionLevel{
        SAFE(0), UNSAFE(1);
        private int count;
        ConcurrentConnectionLevel(int count)
        {
            this.count = count;
        }
    }
    @Override
    boolean isSafe(Authentication authentication) throws ConcurrentConnectionRuntimeException, AuthenticationRuntimeException
    {
        return SAFE.count == get(authentication).count;
    }

    @Override
    boolean isUnSafe(Authentication authentication) throws ConcurrentConnectionRuntimeException, AuthenticationRuntimeException
    {
        Integer count = get(authentication).count;
        return  UNSAFE.count <= count;
    }

/*
    // 동시 접속 시도 횟수 초과 여부
    public boolean isExceedPermit(EmployeeAuthentication authentication) throws AuthenticationNullPointerException, ConCurrentConnectionNotFoundException
    {
        // 동시 접속 시도 허용 횟수
        return ConcurrentConnectionStatus.EXCEED == ((EmployeeConcurrentConnection) get(authentication)).getStatus();
    }
*/
}

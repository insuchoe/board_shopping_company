package com.brandedCompany.concurrentConnection;

import com.brandedCompany.authentication.Authentication;
import com.brandedCompany.authentication.exception.AuthenticationNullPointerException;
import com.brandedCompany.authentication.exception.AuthenticationRuntimeException;
import com.brandedCompany.concurrentConnection.exception.ConCurrentConnectionNotFoundException;
import com.brandedCompany.concurrentConnection.exception.ConcurrentConnectionRuntimeException;
import com.brandedCompany.concurrentConnection.exception.ConnectionExceedException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class ConcurrentConnectionManager
{
    private final Map<Authentication, ConcurrentConnection> REPOSITORY = new HashMap<>();

    public void save(Authentication authentication, ConcurrentConnection concurrentConnection) throws AuthenticationRuntimeException, ConcurrentConnectionRuntimeException
    {

        if (Optional.ofNullable(authentication).isEmpty())
            throw new AuthenticationNullPointerException();

        // 동시 접속 이력이 없으면
        if (!has(authentication))
            // 동시 접속 추가
            REPOSITORY.put(authentication, concurrentConnection);


    }



    public ConcurrentConnection get(Authentication authentication) throws AuthenticationRuntimeException, ConcurrentConnectionRuntimeException
    {
        if (Optional.ofNullable(authentication)
                    .isEmpty())
            throw new AuthenticationNullPointerException();
        // 동시 접속한 적이 없는 고객이라면
        if (!has(authentication))
            throw new ConCurrentConnectionNotFoundException("no concurrent connection.");

        // 동시 접속 반환
        return REPOSITORY.get(authentication);
    }

    public boolean has(Authentication authentication)
    {
        return REPOSITORY.containsKey(authentication);
    }

    public void remove(Authentication authentication) throws ConcurrentConnectionRuntimeException
    {
        if (!has(authentication))
            throw new ConCurrentConnectionNotFoundException("no concurrent connection.");

        // 동시 접속 삭제
        REPOSITORY.remove(authentication);
    }

    public void refresh(Authentication authentication) throws ConcurrentConnectionRuntimeException, AuthenticationRuntimeException
    {
        ConcurrentConnection concurrentConnection = get(authentication);
        if (isUnSafe(authentication))
            concurrentConnection.changeStatus(ConcurrentConnectionStatus.EXCEED);

        else if (isSafe(authentication))
            concurrentConnection.changeStatus(ConcurrentConnectionStatus.NONE);
    }

    public void check(Authentication authentication) throws ConcurrentConnectionRuntimeException, AuthenticationRuntimeException
    {
        if (isSafe(authentication))
            return;
        if (isUnSafe(authentication))
            throw new ConnectionExceedException("The number of concurrent connections allowed has been exceeded.");

    }

    abstract boolean isSafe(Authentication authentication) throws ConcurrentConnectionRuntimeException, AuthenticationRuntimeException;

    abstract boolean isUnSafe(Authentication authentication) throws ConcurrentConnectionRuntimeException, AuthenticationRuntimeException;

}

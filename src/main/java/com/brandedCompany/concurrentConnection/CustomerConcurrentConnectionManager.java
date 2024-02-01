package com.brandedCompany.concurrentConnection;

import com.brandedCompany.authentication.Authentication;
import com.brandedCompany.authentication.exception.AuthenticationRuntimeException;
import com.brandedCompany.concurrentConnection.exception.ConcurrentConnectionCumulativeException;
import com.brandedCompany.concurrentConnection.exception.ConcurrentConnectionExceedException;
import com.brandedCompany.concurrentConnection.exception.ConcurrentConnectionRuntimeException;
import com.brandedCompany.concurrentConnection.exception.ConnectionCumulativeException;
import org.springframework.stereotype.Component;

@Component

public class CustomerConcurrentConnectionManager extends ConcurrentConnectionManager
{
    private final ConcurrentConnectionLevel SAFE = ConcurrentConnectionLevel.SAFE;
    private final ConcurrentConnectionLevel UNSAFE = ConcurrentConnectionLevel.UNSAFE;

    //    private final Map<CustomerAuthentication, CustomerConcurrentConnection> REPOSITORY = new HashMap<>();

    private enum ConcurrentConnectionLevel
    {
        SAFE(0),
        UNSAFE(3);
        private final int count;

        ConcurrentConnectionLevel(int count)
        {
            this.count = count;
        }
    }


    @Override
    public void refresh(Authentication authentication) throws ConcurrentConnectionRuntimeException, AuthenticationRuntimeException
    {
        super.refresh(authentication);
        if(SAFE.count<get(authentication).count&&get(authentication).count<UNSAFE.count)
        {
            ConcurrentConnection concurrentConnection = get(authentication);
            concurrentConnection.changeStatus(ConcurrentConnectionStatus.CUMULATIVE);
        }
    }

    @Override
    public void check(Authentication authentication) throws ConcurrentConnectionRuntimeException, AuthenticationRuntimeException
    {
        super.check(authentication);
        if(SAFE.count<get(authentication).count&&get(authentication).count<UNSAFE.count)
            throw new ConcurrentConnectionCumulativeException();
    }

    @Override
    boolean isSafe(Authentication authentication) throws ConcurrentConnectionRuntimeException, AuthenticationRuntimeException
    {
        return SAFE.count == get(authentication).count;
    }


    @Override
    boolean isUnSafe(Authentication authentication) throws ConcurrentConnectionRuntimeException, AuthenticationRuntimeException
    {
        return UNSAFE.count <= get(authentication).count;
    }
    /*
    @Override
    public void save(Authentication authentication, ConcurrentConnection concurrentConnection) throws AuthenticationNullPointerException
    {


        if (Optional.ofNullable(authentication).isEmpty())
            throw new AuthenticationNullPointerException();

        // 고객 동시 접속 타입으로 변환
        CustomerConcurrentConnection customerConcurrentConnection = (CustomerConcurrentConnection) concurrentConnection;
        // 동시 접속 이력이 있으면
        // 동시 접속 추가
        if (has(authentication))
            // 동시 접속 횟수 + 1
            customerConcurrentConnection.incrementCount();
        else
            // 동시 접속 추가
            REPOSITORY.put((CustomerAuthentication) authentication, (CustomerConcurrentConnection) concurrentConnection);

    }
    */
    // 동시 접속 여부
    /*
    public boolean has(Authentication authentication)
    {
        return REPOSITORY.containsKey(authentication);
    }
    */
    /*
    // 동시 접속 반환
    @Override
    public ConcurrentConnection get(Authentication authentication) throws AuthenticationNullPointerException, ConCurrentConnectionNotFoundException
    {
        if (Optional.ofNullable(authentication).isEmpty())
            throw new AuthenticationNullPointerException();
        // 동시 접속한 적이 없는 고객이라면
        if (!has(authentication))
            throw new ConCurrentConnectionNotFoundException("no concurrent connection.");

        // 동시 접속 반환
        return REPOSITORY.get(authentication);
    }
    */
    /*
    // 동시 접속 삭제
    @Override
    public void remove(Authentication authentication) throws ConCurrentConnectionNotFoundException
    {
        // 동시 접속한 적이 없으면
        if (!has(authentication))
            throw new ConCurrentConnectionNotFoundException("no concurrent connection.");

        // 동시 접속 삭제
        REPOSITORY.remove(authentication);
    }
    */

    /*
    @Override
    public void refresh(Authentication authentication) throws ConcurrentConnectionRuntimeException, AuthenticationRuntimeException
    {
        ConcurrentConnection concurrentConnection = get(authentication);

        if(isError(authentication))
            concurrentConnection.changeStatus(ConcurrentConnectionStatus.EXCEED);
        else if (isWarn(authentication))
            concurrentConnection.changeStatus(ConcurrentConnectionStatus.CUMULATIVE);
        else if (isSafe(authentication))
            concurrentConnection.changeStatus(ConcurrentConnectionStatus.NONE);
    }
    */

    /*
    // 동시 접속 시도 횟수 초과 여부
    public boolean isExceedPermit(CustomerAuthentication authentication) throws AuthenticationNullPointerException, ConCurrentConnectionNotFoundException
    {
        // 동시 접속 시도 허용 횟수
        return ConcurrentConnectionStatus.EXCEED == ((CustomerConcurrentConnection) get(authentication)).getStatus();
    }
    */


}

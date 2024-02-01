package com.brandedCompany.authentication;

import com.brandedCompany.authentication.exception.AuthenticationNotFoundException;
import com.brandedCompany.authentication.exception.AuthenticationNullPointerException;
import com.brandedCompany.authentication.exception.AuthenticationRuntimeException;
import com.brandedCompany.authentication.util.AuthenticationUtil;
import com.brandedCompany.domain.Employee;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@Component
@NoArgsConstructor
public class EmployeeAuthenticationManager implements AuthenticationManager
{
    final Logger logger = LoggerFactory.getLogger(EmployeeAuthenticationManager.class);

    // 직원 인증 리포지토리
    private final Map<BigInteger, EmployeeAuthentication> REPOSITORY = new HashMap<>();
    private  AuthenticationUtil util;

    @Autowired
    public void setUtil(AuthenticationUtil util)
    {
        this.util = util;
    }


    @Override
    public void addAuthentication(Authentication authentication) throws AuthenticationRuntimeException
    {
        Employee connector = ((EmployeeAuthentication) authentication).getTarget();

        //  인증 객체 Map 에 추가
        REPOSITORY.put(connector.getEmployeeId(), (EmployeeAuthentication) authentication);
        logger.info("인증 추가 : "+ authentication);

    }

    @Override
    public void removeAuthentication(Authentication authentication) throws AuthenticationNotFoundException
    {
        util.checkEmployeeAuthentication((EmployeeAuthentication) authentication);

        if (hasAuthentication(authentication))
        {
            REPOSITORY.remove(((EmployeeAuthentication) authentication).getTarget()
                                                                       .getEmployeeId());
            logger.info("remove authentication : "+authentication);
        }
    }

    public Authentication getAuthentication(BigInteger authenticationId) throws AuthenticationNullPointerException
    {
        if (!hasAuthentication(authenticationId))
            throw new AuthenticationNullPointerException();
        return REPOSITORY.get(authenticationId);

    }

    public void removeAuthenticationById(BigInteger authenticationId) throws AuthenticationNullPointerException
    {
        if (!hasAuthentication(authenticationId))
        {
            logger.error("hasn't authentication by id : "+authenticationId);
            throw new AuthenticationNullPointerException();
        }
        logger.info("remove authentication : "+ REPOSITORY.get(authenticationId));
        REPOSITORY.remove(authenticationId);
    }

    @Override
    public boolean hasAuthentication(Authentication authentication)
    {
        EmployeeAuthentication employeeAuthentication = (EmployeeAuthentication) authentication;
        Employee target =employeeAuthentication.getTarget();
        return REPOSITORY.containsKey(target.getEmployeeId());

    }

    @Override
    public void refreshAuthentication(Authentication authentication) throws AuthenticationRuntimeException
    {
        EmployeeAuthentication employeeAuthentication = (EmployeeAuthentication) authentication;
        if (authentication instanceof EmployeeAuthentication)
            util.checkEmployeeAuthentication(employeeAuthentication);

        // 옵저버에 인증 타겟 객체 전달 -> 인증 상태 실시간 업로드
        employeeAuthentication.update(employeeAuthentication.getTarget());

        // 인증 결과 로깅
        util.logAuthenticationStatus(employeeAuthentication);
    }

    public boolean hasAuthentication(BigInteger key)
    {
        return REPOSITORY.containsKey(key);
    }

}

package com.brandedCompany.authentication;

import com.brandedCompany.authentication.exception.*;
import com.brandedCompany.authentication.util.AuthenticationUtil;
import com.brandedCompany.domain.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;


@Component
public class CustomerAuthenticationManager implements AuthenticationManager
{
    final Logger logger = LoggerFactory.getLogger(CustomerAuthenticationManager.class);

    // 직원 인증 리포지토리
    private final Map<BigInteger, CustomerAuthentication> REPOSITORY = new HashMap<>();
    private  AuthenticationUtil util;

    @Autowired
    public void setUtil(AuthenticationUtil util)
    {
        this.util = util;
    }

    @Override
    public void addAuthentication(Authentication authentication) throws AuthenticationRuntimeException
    {
        Customer connector = ((CustomerAuthentication) authentication).getTarget();

        REPOSITORY.put(connector.getCustomerId(), (CustomerAuthentication) authentication);
        logger.info("인증 추가 : "+ authentication);
    }

    @Override
    public void removeAuthentication(Authentication authentication) throws AuthenticationNotFoundException
    {
        util.checkCustomerAuthentication((CustomerAuthentication) authentication);

        if (hasAuthentication(authentication))
        {

            REPOSITORY.remove(((CustomerAuthentication) authentication).getTarget()
                                                                       .getCustomerId());
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
        CustomerAuthentication customerAuthentication = (CustomerAuthentication) authentication;
        Customer target =customerAuthentication.getTarget();
        return REPOSITORY.containsKey(target.getCustomerId());
    }

    @Override
    public void refreshAuthentication(Authentication authentication) throws AuthenticationRuntimeException
    {
        CustomerAuthentication customerAuthentication = (CustomerAuthentication) authentication;
        if (authentication instanceof CustomerAuthentication)
            util.checkCustomerAuthentication(customerAuthentication);

        // 옵저버에 인증 타겟 객체 전달 -> 인증 상태 실시간 업로드
        customerAuthentication.update(customerAuthentication.getTarget());

        // 인증 결과 로깅
        util.logAuthenticationStatus(customerAuthentication);
    }

    public boolean hasAuthentication(BigInteger key)
    {
        return REPOSITORY.containsKey(key);
    }
}

package com.brandedCompany.exception;

import com.brandedCompany.domain.Domain;
import com.brandedCompany.util.DomainUtils.TABLE;

import java.math.BigInteger;

public class DomainNotFoundException extends RuntimeException{
    public DomainNotFoundException()
    {
        super("찾을 수 없는 도메인");
    }
    public DomainNotFoundException(String message)
    {
        super("찾을 수 없는 도메인 " + message);
    }

    public DomainNotFoundException(TABLE table, BigInteger id)
    {
        super(table.name()+" 식별번호 : "+id);
    }

    public DomainNotFoundException( Domain domain)
    {
        super(domain.toString());
    }
}

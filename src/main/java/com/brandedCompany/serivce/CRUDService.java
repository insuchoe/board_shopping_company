package com.brandedCompany.serivce;

import com.brandedCompany.domain.Domain;
import com.brandedCompany.exception.DomainNotFoundException;
import com.brandedCompany.util.DomainUtils;

import java.math.BigInteger;
import java.util.Collection;

public interface CRUDService {
    BigInteger count(DomainUtils.TABLE table) throws Exception;

    void delete(DomainUtils.TABLE table) throws Exception;

    boolean delete(DomainUtils.TABLE table, BigInteger... ids) throws Exception;

    boolean  isExist(DomainUtils.TABLE table, BigInteger... ids) throws Exception;

    Collection<? extends Domain> select(DomainUtils.TABLE table) throws Exception;

    Collection<? extends Domain> selectAll(DomainUtils.TABLE table, BigInteger... ids) throws Exception;

    Domain select(DomainUtils.TABLE table, BigInteger... ids) throws DomainNotFoundException,NullPointerException,ClassNotFoundException, Exception;

    boolean update(Domain domain) throws Exception;

    boolean insert(Domain domain) throws Exception;

}

package com.brandedCompany.repository;

import com.brandedCompany.domain.Domain;
import com.brandedCompany.exception.DomainNotFoundException;
import com.brandedCompany.util.DomainUtils.TABLE;

import java.math.BigInteger;
import java.util.Collection;

public interface CRUDRepository extends Repository {

    BigInteger count(TABLE table) throws Exception;

    void delete(TABLE table) throws Exception;

    boolean delete(TABLE table, BigInteger... ids) throws Exception;

    boolean isExist(TABLE table, BigInteger... ids) throws Exception;

    Collection<? extends Domain> select(TABLE table) throws Exception;

    Collection<? extends Domain> selectAll(TABLE table, BigInteger... ids) throws Exception;

    Domain select(TABLE table, BigInteger... ids) throws Exception;

    boolean update(Domain domain) throws Exception;

    boolean insert(Domain domain) throws Exception;


}

package com.brandedCompany.serivce;


import com.brandedCompany.domain.Domain;
import com.brandedCompany.util.DomainUtils;

import java.math.BigInteger;
import java.util.Collection;

public interface OptionService extends PagingAndSortingService{

    Collection<? extends Domain> selectByName(DomainUtils.TABLE table, String name, DomainUtils.NameLocation location) throws Exception;

    Collection<?extends
            Domain> selectBoardByEmpId(BigInteger employeeId) throws Exception;

    BigInteger selectNextSequence(DomainUtils.TABLE table) throws Exception ;


    BigInteger selectNextBoardId() throws Exception;
    BigInteger selectLastBoardId() throws Exception;


    BigInteger selectLastCommentId() throws Exception;
}

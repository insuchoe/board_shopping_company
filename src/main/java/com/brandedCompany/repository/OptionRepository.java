package com.brandedCompany.repository;


import com.brandedCompany.domain.Board;
import com.brandedCompany.domain.Domain;
import com.brandedCompany.util.DomainUtils;
import com.brandedCompany.util.DomainUtils.TABLE;

import java.math.BigInteger;
import java.util.Collection;

public interface OptionRepository extends PagingAndSortingRepository {
    Collection<? extends Domain> selectByName(TABLE table, String name, DomainUtils.NameLocation location) throws Exception;

    Collection<Board> selectBoardByEmpId(BigInteger employeeId) throws Exception;

    BigInteger selectNextSequence(TABLE table) throws Exception ;

    BigInteger selectNextBoardId() throws Exception;
    BigInteger selectLastBoardId() throws Exception;

    BigInteger selectLastCommentId() throws Exception;

}

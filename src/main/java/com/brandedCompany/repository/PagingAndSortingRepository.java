package com.brandedCompany.repository;

import com.brandedCompany.domain.*;
import com.brandedCompany.domain.searchCondition.SearchCondition;
import com.brandedCompany.util.DomainUtils;

import java.math.BigInteger;
import java.util.Collection;

public interface PagingAndSortingRepository extends CRUDRepository{
    BigInteger count(SearchCondition condition) throws ClassNotFoundException;
    Collection<? extends Domain> pageAndSort( SearchCondition searchCondition) throws Exception;

    Board selectRecentBoard() throws Exception;

    boolean isExistComment(BigInteger boardId) throws Exception;

    void increaseViews(BigInteger boardId) throws Exception;
}

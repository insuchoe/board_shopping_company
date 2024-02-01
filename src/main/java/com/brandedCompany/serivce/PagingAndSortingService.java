package com.brandedCompany.serivce;

import com.brandedCompany.domain.*;
import com.brandedCompany.domain.searchCondition.SearchCondition;

import java.math.BigInteger;
import java.util.Collection;

public interface PagingAndSortingService  extends CRUDService{

    BigInteger count(SearchCondition condition) throws ClassNotFoundException;

    Collection<? extends Domain> pageAndSort(SearchCondition condition) throws Exception;

    Board selectRecentBoard() throws Exception;

    boolean isExistComment(BigInteger boardId) throws Exception;

    void increaseViews(BigInteger boardId) throws Exception;
}


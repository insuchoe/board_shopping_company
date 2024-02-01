package com.brandedCompany.domain.handler;

import com.brandedCompany.domain.searchCondition.OrderHistorySearchCondition;
import com.brandedCompany.domain.searchCondition.SearchCondition;
import com.brandedCompany.serivce.PagingAndSortingService;
import lombok.EqualsAndHashCode;

import java.math.BigInteger;

public class OrderHistoryPageHandler extends PageHandler {

    public OrderHistoryPageHandler(BigInteger TOTAL_COUNT, SearchCondition searchCondition) {
        super(10, TOTAL_COUNT, searchCondition);
    }

    @Override
    public String getQueryString() {
        return getSearchCondition().getQueryString();
    }

    @Override
    public String getQueryString(Integer page) {
        return getSearchCondition().getQueryString(page);
    }

    public OrderHistorySearchCondition getSearchCondition() {
        return (OrderHistorySearchCondition) this.searchCondition;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof OrderHistoryPageHandler)) return false;
        OrderHistoryPageHandler orderHistoryPageHandler = (OrderHistoryPageHandler) obj;
        return this.getQueryString()
                   .equals(orderHistoryPageHandler.getQueryString());
    }

    @Override
    public int hashCode()
    {
        return getQueryString().hashCode();
    }
}

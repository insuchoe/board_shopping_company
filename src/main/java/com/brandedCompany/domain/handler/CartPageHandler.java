package com.brandedCompany.domain.handler;

import com.brandedCompany.domain.searchCondition.CartSearchCondition;
import com.brandedCompany.domain.searchCondition.SearchCondition;
import lombok.ToString;

import java.math.BigInteger;

public class CartPageHandler extends PageHandler {

    public CartPageHandler(BigInteger TOTAL_COUNT, SearchCondition searchCondition) {
        super(10, TOTAL_COUNT, searchCondition);

    }

    @Override
    public String getQueryString() {
        return getSearchCondition().getQueryString();
    }

    @Override
    public String getQueryString(Integer PAGE) {
        return getSearchCondition().getQueryString(PAGE);

    }

    public CartSearchCondition getSearchCondition() {
        return (CartSearchCondition) this.searchCondition;
    }

    @Override public String toString()
    {
        return "CartPageHandler{" + "searchCondition=" + searchCondition + ", NAV_SIZE=" + NAV_SIZE + ", TOTAL_COUNT=" + TOTAL_COUNT + ", TOTAL_PAGE=" + TOTAL_PAGE + ", BEGIN_PAGE=" + BEGIN_PAGE + ", END_PAGE=" + END_PAGE + ", showPrev=" + showPrev + ", showNext=" + showNext + '}';
    }
}

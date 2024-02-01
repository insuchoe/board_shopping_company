package com.brandedCompany.domain.handler;

import com.brandedCompany.domain.searchCondition.ProductSearchCondition;
import lombok.ToString;

import java.math.BigInteger;


public class ProductPageHandler extends PageHandler {

    public ProductPageHandler(BigInteger TOTAL_COUNT, ProductSearchCondition searchCondition) {
        super(10, TOTAL_COUNT, searchCondition);
    }

    public ProductSearchCondition getSearchCondition() {
        return (ProductSearchCondition) super.searchCondition;
    }


    @Override
    public String getQueryString() {
        return getSearchCondition().getQueryString();
    }

    @Override
    public String getQueryString(Integer PAGE) {
        return getSearchCondition().getQueryString(PAGE);

    }

    public BigInteger getProductId() {
        return getSearchCondition().getProductId();
    }

    public ProductSearchCondition.ProductOrderTarget getTarget() {
    return getSearchCondition().getTarget();
    }

    public String getKeyword() {
        return getSearchCondition().getKeyword();
    }

    public ProductSearchCondition.ProductSortDirection getDirection() {

        return getSearchCondition().getDirection();
    }

    @Override
    public String toString() {
        return "ProductPageHandler{" +
                "searchCondition=" + searchCondition +
                ", NAV_SIZE=" + NAV_SIZE +
                ", TOTAL_COUNT=" + TOTAL_COUNT +
                ", TOTAL_PAGE=" + TOTAL_PAGE +
                ", BEGIN_PAGE=" + BEGIN_PAGE +
                ", END_PAGE=" + END_PAGE +
                ", showPrev=" + showPrev +
                ", showNext=" + showNext +
                '}';
    }
}

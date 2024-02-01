package com.brandedCompany.domain.handler;

import com.brandedCompany.domain.searchCondition.InventorySearchCondition;
import com.brandedCompany.domain.searchCondition.ProductSearchCondition;

import java.math.BigInteger;

public class InventoryPageHandler extends PageHandler {

    public InventoryPageHandler(BigInteger TOTAL_COUNT, InventorySearchCondition searchCondition) {
        super(10, TOTAL_COUNT, searchCondition);
    }

    public InventorySearchCondition getSearchCondition() {
        return (InventorySearchCondition) super.searchCondition;
    }


    @Override
    public String getQueryString() {
        return searchCondition.getQueryString();
    }

    @Override
    public String getQueryString(Integer PAGE) {
        return searchCondition.getQueryString(PAGE);

    }

    public BigInteger getProductId() {
        return getSearchCondition().getProductId();
    }

    public InventorySearchCondition.InventorySortTarget getSortTarget() {
        return getSearchCondition().getSortTarget();
    }

    public String getKeyword() {
        return getSearchCondition().getKeyword();
    }

    public InventorySearchCondition.InventorySortDirection getSortDirection() {
        return getSearchCondition().getSortDirection();
    }

    public BigInteger getWarehouseId() {
        return getSearchCondition().getWarehouseId();
    }
}

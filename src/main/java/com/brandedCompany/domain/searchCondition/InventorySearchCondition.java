package com.brandedCompany.domain.searchCondition;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Optional;

@Getter
public class InventorySearchCondition extends SearchCondition {
    public InventorySearchCondition(Integer page, String keyword, InventorySortDirection sortDirection, InventorySortTarget sortTarget) {
        // MIN_PAGE_SIZE <= PAGE_SIZE <= MAX_PAGE_SIZE
        this(page);
        this.sortDirection = sortDirection;
        this.keyword = keyword;
        this.sortTarget = sortTarget;
    }

    public InventorySearchCondition(Integer page, String keyword, String sortDirection, String sortTarget) {
        this(page);
        Arrays.stream(InventorySortDirection.values()).filter(srtDcn -> sortDirection.equals(srtDcn.name())).findAny().ifPresent(
                srtDcn -> this.sortDirection = srtDcn);
        Arrays.stream(InventorySortTarget.values()).filter(srtTrg -> sortTarget.equals(srtTrg.name())).findAny().ifPresent(
                srtTrg -> this.sortTarget = srtTrg);
        this.keyword = keyword;
    }


    public InventorySearchCondition(InventorySearchConditionBuilder builder) {
        super(20,10,100,20,builder.getPAGE());
        this.keyword = builder.keyword;
        this.sortTarget=builder.sortTarget;this.sortDirection=builder.sortDirection;
        this.productId = builder.productId;
        this.warehouseId=builder.warehouseId;
    }

    // 정렬 방향
    public enum InventorySortDirection {
        ASC, DESC;


    }

    // 정렬 기준
    public enum InventorySortTarget {
        QUANTITY, // 수량
        LIST_PRICE, // 가격
        PRODUCT_NAME, // 상품이름
        REGION_NAME, // 지역이름
        COUNTRY_NAME, // 나라이름
        ADDRESS, // 주소지
        CITY, //도시
        WAREHOUSE_NAME // 창고이름
    }

    private BigInteger productId, warehouseId;
    private InventorySortTarget sortTarget;
    private String keyword;
    private InventorySortDirection sortDirection;

    public static class InventorySearchConditionBuilder extends SearchCondition {


        public InventorySearchConditionBuilder() {
            super(20,10,100,20,1);
        }


        // 정렬 방향
        public enum InventorySortDirection {
            ASC, DESC;


        }

        // 정렬 기준
        public enum InventorySortTarget {
            QUANTITY, // 수량
            LIST_PRICE, // 가격
            PRODUCT_NAME, // 상품이름
            REGION_NAME, // 지역이름
            COUNTRY_NAME, // 나라이름
            ADDRESS, // 주소지
            CITY, //도시
            WAREHOUSE_NAME // 창고이름
        }

        private BigInteger productId, warehouseId;
        private InventorySearchCondition.InventorySortTarget sortTarget;
        private String keyword;
        private InventorySearchCondition.InventorySortDirection sortDirection;

        public InventorySearchConditionBuilder page(Integer page) {
            this.PAGE = page;
            return this;
        }

        public InventorySearchConditionBuilder productId(BigInteger productId) {
            this.productId = productId;
            return this;
        }

        public InventorySearchConditionBuilder warehouseId(BigInteger warehouseId) {
            this.warehouseId = warehouseId;
            return this;
        }

        public InventorySearchConditionBuilder sortTarget(InventorySearchCondition.InventorySortTarget sortTarget) {
            this.sortTarget = sortTarget;
            return this;
        }

        public InventorySearchConditionBuilder keyword(String keyword) {
            this.keyword = keyword;
            return this;
        }

        public InventorySearchConditionBuilder sortDirection(InventorySearchCondition.InventorySortDirection sortDirection) {
            this.sortDirection = sortDirection;
            return this;
        }

        public InventorySearchCondition build() {
            return new InventorySearchCondition(this);
        }


        @Override
        public String getQueryString(Integer page) {
            UriComponentsBuilder queryString = UriComponentsBuilder.newInstance();
            if (Optional.ofNullable(keyword).isPresent() && !keyword.isBlank())
                queryString.queryParam("keyword", keyword);
            if (Optional.ofNullable(sortTarget).isPresent() && !sortTarget.name().isBlank())
                queryString.queryParam("sortTarget", sortTarget.name());
            if (Optional.ofNullable(sortDirection).isPresent() && !sortDirection.name().isBlank())
                queryString.queryParam("sortDirection", sortDirection.name());
            if (0 != page)
                queryString.queryParam("page" , page)
                        .build().toString();
            return queryString.build().toString();

        }

        @Override
        public String getQueryString() {
            UriComponentsBuilder queryString = UriComponentsBuilder.newInstance();
            if (Optional.ofNullable(keyword).isPresent() && !keyword.isBlank())
                queryString.queryParam("keyword", keyword);
            if (Optional.ofNullable(sortTarget).isPresent() && !sortTarget.name().isBlank())
                queryString.queryParam("sortTarget", sortTarget.name());
            if (Optional.ofNullable(sortDirection).isPresent() && !sortDirection.name().isBlank())
                queryString.queryParam("sortDirection", sortDirection.name());
            if (0 != PAGE)
                queryString.queryParam("page", PAGE)
                        .build().toString();
            return queryString.build().toString();
        }


        public String getQueryString(Integer page, InventorySearchCondition.InventorySortTarget sortTarget, String keyword, InventorySearchCondition.InventorySortDirection sortDirection) {
            UriComponentsBuilder queryString = UriComponentsBuilder.newInstance();
            if (Optional.ofNullable(keyword).isPresent() && !keyword.isBlank())
                queryString.queryParam("keyword", keyword);
            if (Optional.ofNullable(sortTarget).isPresent() && !sortTarget.name().isBlank())
                queryString.queryParam("sortTarget", sortTarget.name());
            if (Optional.ofNullable(sortDirection).isPresent() && !sortDirection.name().isBlank())
                queryString.queryParam("sortDirection", sortDirection.name());
            if (0 != page)
                queryString.queryParam("page", page)
                        .build().toString();
            return queryString.build().toString();
        }

    }

    public InventorySearchCondition() {
        this(1);
    }

    public InventorySearchCondition(Integer page, BigInteger productId, BigInteger warehouseId) {
        this(page);
        this.productId = productId;
        this.warehouseId = warehouseId;
    }

    public InventorySearchCondition(BigInteger productId, BigInteger warehouseId) {
        this(1);
        this.productId = productId;
        this.warehouseId = warehouseId;
    }

    public InventorySearchCondition(Integer page) {
        // MIN_PAGE_SIZE <= PAGE_SIZE <= MAX_PAGE_SIZE
        super(20, 10, 100, 20,
                page);
    }

    @Override
    public String getQueryString(Integer page) {
        UriComponentsBuilder queryString = UriComponentsBuilder.newInstance();
        if (Optional.ofNullable(keyword).isPresent() && !keyword.isBlank())
            queryString.queryParam("keyword", keyword);
        if (Optional.ofNullable(sortTarget).isPresent() && !sortTarget.name().isBlank())
            queryString.queryParam("sortTarget", sortTarget.name());
        if (Optional.ofNullable(sortDirection).isPresent() && !sortDirection.name().isBlank())
            queryString.queryParam("sortDirection", sortDirection.name());
        if (0 != page)
            queryString.queryParam("page", page)
                    .build().toString();
        return queryString.build().toString();

    }

    @Override
    public String getQueryString() {
        UriComponentsBuilder queryString = UriComponentsBuilder.newInstance();
        if (Optional.ofNullable(keyword).isPresent() && !keyword.isBlank())
            queryString.queryParam("keyword", keyword);
        if (Optional.ofNullable(sortTarget).isPresent() && !sortTarget.name().isBlank())
            queryString.queryParam("sortTarget", sortTarget.name());
        if (Optional.ofNullable(sortDirection).isPresent() && !sortDirection.name().isBlank())
            queryString.queryParam("sortDirection", sortDirection.name());
        if (0 != PAGE)
            queryString.queryParam("page", PAGE)
                    .build().toString();
        return queryString.build().toString();
    }


    public String getQueryString(Integer page, InventorySortTarget sortTarget, String keyword, InventorySortDirection sortDirection) {
        return UriComponentsBuilder.newInstance()
                .queryParam("page", page)
                .queryParam("sortTarget", sortTarget)
                .queryParam("keyword", keyword)
                .queryParam("sortDirection", sortDirection)
                .build().toString();
    }



    public int getOffset() {
        return (PAGE - 1) * PAGE_SIZE;
    }

    public String getClassSimpleName() {
        return this.getClass().getSimpleName();
    }


}

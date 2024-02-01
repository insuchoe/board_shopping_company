package com.brandedCompany.domain.searchCondition;

import lombok.Getter;
import org.springframework.lang.Nullable;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public class ProductSearchCondition extends SearchCondition {
    public ProductSearchCondition(ProductSearchConditionBuilder builder) {
        super(20, 10, 100, 20,
                builder.getPAGE());
        this.productId = builder.productId;
        this.target = builder.target;
        this.keyword = builder.keyword;
        this.direction = builder.direction;
    }

    public enum ProductSortDirection {
        ASC, DESC;
    }

    public enum ProductOrderTarget {
        PRODUCT_NAME,//상품이름
        LIST_PRICE;//상품가격
    }

    private final BigInteger productId;
    private final ProductOrderTarget target;
    private final String keyword;
    private final ProductSortDirection direction;

    public static class ProductSearchConditionBuilder extends SearchCondition {

        public ProductSearchConditionBuilder() {
            super(20, 10, 100, 20,
                    1);
        }



        private BigInteger productId;
        private ProductOrderTarget target;
        private String keyword;
        private ProductSortDirection direction;

        public ProductSearchConditionBuilder page(Integer page) {
            this.PAGE = page;
            return this;
        }

        public ProductSearchConditionBuilder productId(BigInteger productId) {
            this.productId = productId;
            return this;
        }

        public ProductSearchConditionBuilder target(ProductOrderTarget target) {
            this.target = target;
            return this;

        }

        public ProductSearchConditionBuilder keyword(String keyword) {
            this.keyword = keyword;
            return this;
        }

        public ProductSearchConditionBuilder direction(ProductSortDirection direction) {
            this.direction = direction;
            return this;
        }

        public ProductSearchCondition build() {
            return new ProductSearchCondition(this);
        }

        @Override
        public String getQueryString() {
            UriComponentsBuilder queryString = UriComponentsBuilder.newInstance();

            if (Optional.ofNullable(productId).isPresent())
                queryString.queryParam("productId", productId);
            if (Optional.ofNullable(target).isPresent())
                queryString.queryParam("target", target);
            if (Optional.ofNullable(keyword).isPresent())
                queryString.queryParam("keyword", keyword);
            if (Optional.ofNullable(direction).isPresent())
                queryString.queryParam("direction", direction);
            if(0!=PAGE)
            queryString.queryParam("page", PAGE);
            return queryString.build().toString();
        }

        @Override
        public String getQueryString(Integer page) {
            UriComponentsBuilder queryString = UriComponentsBuilder.newInstance();

            if (Optional.ofNullable(productId).isPresent())
                queryString.queryParam("productId", productId);
            if (Optional.ofNullable(target).isPresent())
                queryString.queryParam("target", target);
            if (Optional.ofNullable(keyword).isPresent())
                queryString.queryParam("keyword", keyword);
            if (Optional.ofNullable(direction).isPresent())
                queryString.queryParam("direction", direction);
            if(0!=page)
                queryString.queryParam("page", page);
            return queryString.build().toString();
        }

    }


    @Override
    public String getQueryString() {
        UriComponentsBuilder queryString = UriComponentsBuilder.newInstance();

        if (Optional.ofNullable(productId).isPresent())
            queryString.queryParam("productId", productId);
        if (Optional.ofNullable(target).isPresent())
            queryString.queryParam("target", target);
        if (Optional.ofNullable(keyword).isPresent())
            queryString.queryParam("keyword", keyword);
        if (Optional.ofNullable(direction).isPresent())
            queryString.queryParam("direction", direction);
        if(0!=PAGE)
            queryString.queryParam("page", PAGE);
        return queryString.build().toString();
    }

    @Override
    public String getQueryString(Integer page) {
        UriComponentsBuilder queryString = UriComponentsBuilder.newInstance();

        if (Optional.ofNullable(productId).isPresent())
            queryString.queryParam("productId", productId);
        if (Optional.ofNullable(target).isPresent())
            queryString.queryParam("target", target);
        if (Optional.ofNullable(keyword).isPresent())
            queryString.queryParam("keyword", keyword);
        if (Optional.ofNullable(direction).isPresent())
            queryString.queryParam("direction", direction);
        if(0!=page)
            queryString.queryParam("page", page);
        return queryString.build().toString();
    }

    public int getOffset() {
        return (PAGE - 1) * PAGE_SIZE;
    }

    public String getClassSimpleName() {
        return this.getClass().getSimpleName();
    }


}

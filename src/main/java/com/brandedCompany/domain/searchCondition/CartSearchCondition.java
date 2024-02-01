package com.brandedCompany.domain.searchCondition;

import com.brandedCompany.domain.Cart;
import lombok.Getter;
import org.springframework.scheduling.config.CronTask;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.math.BigInteger;
import java.util.Optional;

@Getter
public class CartSearchCondition extends SearchCondition {
    private BigInteger customerId;

    public CartSearchCondition(CartSearchConditionBuilder builder) {
        super(12, 6,
                20, 12, builder.getPAGE());

        this.customerId = builder.customerId;
        this.PAGE = builder.PAGE;
    }

    public CartSearchCondition() {
        super(12, 6,
                20, 12, 1);
    }

    public CartSearchCondition(Integer page) {
        super(12, 6,
                20, 12, page);
    }

    public static class CartSearchConditionBuilder extends SearchCondition {
        private BigInteger customerId, productId;

        public CartSearchConditionBuilder() {
            super(12, 6,
                    20, 12, 1);
        }

        public CartSearchConditionBuilder page(Integer page) {
            this.PAGE = page;
            return this;
        }

        public CartSearchConditionBuilder customerId(BigInteger customerId) {
            this.customerId = customerId;
            return this;
        }

        public CartSearchConditionBuilder productId(BigInteger productId) {
            this.productId = productId;
            return this;
        }

        public CartSearchCondition build() {
            return new CartSearchCondition(this);
        }

        @Override
        public String getQueryString() {
            UriComponentsBuilder queryString = UriComponentsBuilder.newInstance();
            if (Optional.ofNullable(PAGE).isPresent())
                queryString.queryParam("page", PAGE);
            return queryString.build().toString();
        }

        @Override
        public String getQueryString(Integer page) {
            UriComponentsBuilder queryString = UriComponentsBuilder.newInstance();
            setPAGE(page);
            if (Optional.ofNullable(PAGE).isPresent())
                queryString.queryParam("page", PAGE);
            return queryString.build().toString();
        }
    }

    @Override
    public String getQueryString() {
        UriComponentsBuilder queryString = UriComponentsBuilder.newInstance();
        if (Optional.ofNullable(PAGE).isPresent())
            queryString.queryParam("page", PAGE);
        return queryString.build().toString();
    }

    @Override
    public String getQueryString(Integer page) {
        UriComponentsBuilder queryString = UriComponentsBuilder.newInstance();
        setPAGE(page);
        if (Optional.ofNullable(PAGE).isPresent())
            queryString.queryParam("page", PAGE);
        return queryString.build().toString();
    }

    public int getOffset() {
        return (PAGE - 1) * PAGE_SIZE;
    }

    public String getClassSimpleName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return "CartSearchCondition{" + "DEFAULT_PAGE_SIZE=" + DEFAULT_PAGE_SIZE + ", MIN_PAGE_SIZE=" + MIN_PAGE_SIZE + ", MAX_PAGE_SIZE=" + MAX_PAGE_SIZE + ", PAGE_SIZE=" + PAGE_SIZE + ", PAGE=" + PAGE + '}';
    }
}

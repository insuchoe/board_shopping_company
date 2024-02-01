package com.brandedCompany.domain.searchCondition;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigInteger;
import java.util.Optional;

@Getter
@Setter
public class OrderHistorySearchCondition extends SearchCondition
{

    public OrderHistorySearchCondition(Integer monthAgo, OrderHistoryOrderStatus status, OrderHistorySortDirection direction, OrderHistorySortTarget target, String keyword)
    {
        super(12, 6, 20, 12, 1);
        this.monthAgo = monthAgo;
        this.status = status;
        this.direction = direction;
        this.target = target;
        this.keyword = keyword;
    }

    public OrderHistorySearchCondition()
    {
        super(12, 6, 20, 12, 1);
    }

    public OrderHistorySearchCondition(Integer page, Integer monthAgo)
    {
        super(12, 6, 20, 12, page);
        this.monthAgo = monthAgo;
    }

    public OrderHistorySearchCondition(OrderHistorySearchConditionBuilder builder)
    {
        super(12, 6, 20, 12, builder.getPAGE());
        this.customerId=builder.customerId;
        this.monthAgo = builder.monthAgo;
        this.status = builder.status;
        this.direction = builder.direction;
        this.target = builder.target;
        this.keyword = builder.keyword;
    }

    public OrderHistorySearchCondition(int page)
    {
        super(12, 6, 20, 12, page);
        this.PAGE = page;
    }

    public enum OrderHistorySortDirection
    {
        ASC,
        DESC;
    }


    public enum OrderHistorySortTarget
    {
        ORDER_DATE,//주문날짜
        ORDER_STATUS,//주문상태
        ORDERER;//주문자

    }

    public enum OrderHistoryOrderStatus
    {
        Pending,
        Shipped,
        Canceled;
    }

    private BigInteger customerId;
    private Integer monthAgo;
    private OrderHistoryOrderStatus status;
    private OrderHistorySortDirection direction;
    private OrderHistorySortTarget target;
    private String keyword;

    public static class OrderHistorySearchConditionBuilder extends SearchCondition
    {

        public OrderHistorySearchConditionBuilder()
        {
            super(12, 6, 20, 12, 1);
        }

        public enum OrderHistorySortDirection
        {
            ASC,
            DESC;
        }


        public enum OrderHistorySortTarget
        {
            ORDER_DATE,//주문날짜
            ORDER_STATUS,//주문상태
            ORDERER;//주문자

        }


        public enum OrderHistoryOrderStatus
        {
            Pending,
            Shipped,
            Canceled;
        }

        private BigInteger customerId;
        private Integer monthAgo;
        private OrderHistorySearchCondition.OrderHistoryOrderStatus status;
        private OrderHistorySearchCondition.OrderHistorySortDirection direction;
        private OrderHistorySearchCondition.OrderHistorySortTarget target;
        private String keyword;

        public OrderHistorySearchConditionBuilder page(Integer page)
        {
            this.PAGE = page;
            return this;
        }

        public OrderHistorySearchConditionBuilder customerId(BigInteger customerId)
        {
            this.customerId = customerId;
            return this;
        }

        public OrderHistorySearchConditionBuilder monthAgo(Integer monthAgo)
        {
            this.monthAgo = monthAgo;
            return this;
        }

        public OrderHistorySearchConditionBuilder status(OrderHistorySearchCondition.OrderHistoryOrderStatus status)
        {
            this.status = status;
            return this;
        }

        public OrderHistorySearchConditionBuilder direction(OrderHistorySearchCondition.OrderHistorySortDirection direction)
        {
            this.direction = direction;
            return this;
        }

        public OrderHistorySearchConditionBuilder target(OrderHistorySearchCondition.OrderHistorySortTarget target)
        {
            this.target = target;
            return this;
        }

        public OrderHistorySearchConditionBuilder keyword(String keyword)
        {
            this.keyword = keyword;
            return this;
        }

        public OrderHistorySearchCondition build()
        {

            return new OrderHistorySearchCondition(this);
        }

        @Override
        public String getQueryString()
        {
            UriComponentsBuilder queryString = UriComponentsBuilder.newInstance();
            if (0 != PAGE)
                queryString.queryParam("page", PAGE);
            if (Optional.ofNullable(target)
                        .isPresent())
                queryString.queryParam("target", target);
            if (Optional.ofNullable(direction)
                        .isPresent())
                queryString.queryParam("direction", direction);
            if (Optional.ofNullable(status)
                        .isPresent())
                queryString.queryParam("status", status);
            if (Optional.ofNullable(keyword)
                        .isPresent())
                queryString.queryParam("keyword", keyword);
            if (Optional.ofNullable(monthAgo)
                        .isPresent())
                queryString.queryParam("monthAgo", monthAgo);
            return queryString.build()
                              .toString();
        }

        @Override
        public String getQueryString(Integer page)
        {
            UriComponentsBuilder queryString = UriComponentsBuilder.newInstance();
            if (0 != page)
                queryString.queryParam("page", page);
            if (Optional.ofNullable(target)
                        .isPresent())
                queryString.queryParam("target", target);
            if (Optional.ofNullable(direction)
                        .isPresent())
                queryString.queryParam("direction", direction);
            if (Optional.ofNullable(status)
                        .isPresent())
                queryString.queryParam("status", status);
            if (Optional.ofNullable(keyword)
                        .isPresent())
                queryString.queryParam("keyword", keyword);
            if (Optional.ofNullable(monthAgo)
                        .isPresent())
                queryString.queryParam("monthAgo", monthAgo);
            return queryString.build()
                              .toString();
        }
    }

    @Override
    public String getQueryString(Integer page)
    {
        UriComponentsBuilder queryString = UriComponentsBuilder.newInstance();
        if (0 != page)
            queryString.queryParam("page", page);
        if (Optional.ofNullable(target)
                    .isPresent())
            queryString.queryParam("target", target);
        if (Optional.ofNullable(direction)
                    .isPresent())
            queryString.queryParam("direction", direction);
        if (Optional.ofNullable(status)
                    .isPresent())
            queryString.queryParam("status", status);
        if (Optional.ofNullable(keyword)
                    .isPresent())
            queryString.queryParam("keyword", keyword);
        if (Optional.ofNullable(monthAgo)
                    .isPresent())
            queryString.queryParam("monthAgo", monthAgo);
        return queryString.build()
                          .toString();
    }

    @Override
    public String getQueryString()
    {
        OrderHistorySearchConditionBuilder builder = new OrderHistorySearchConditionBuilder();
        UriComponentsBuilder queryString = UriComponentsBuilder.newInstance();
        if (0 != PAGE)
            queryString.queryParam("page", PAGE);
        if (Optional.ofNullable(target)
                    .isPresent())
            queryString.queryParam("target", target);
        if (Optional.ofNullable(direction)
                    .isPresent())
            queryString.queryParam("direction", direction);
        if (Optional.ofNullable(status)
                    .isPresent())
            queryString.queryParam("status", status);
        if (Optional.ofNullable(keyword)
                    .isPresent())
            queryString.queryParam("keyword", keyword);
        if (Optional.ofNullable(monthAgo)
                    .isPresent())
            queryString.queryParam("monthAgo", monthAgo);
        return queryString.build()
                          .toString();
    }

    public int getOffset()
    {
        return (PAGE - 1) * PAGE_SIZE;
    }

    public String getClassSimpleName()
    {
        return this.getClass()
                   .getSimpleName();
    }

}

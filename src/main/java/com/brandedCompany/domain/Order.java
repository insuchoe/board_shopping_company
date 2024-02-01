package com.brandedCompany.domain;

import lombok.*;
import org.springframework.lang.Nullable;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Objects;

@NoArgsConstructor
@ToString
@Getter
public class Order extends Domain {
    private BigInteger orderId, customerId, salesmanId;
    private OrderStatus status;
    private LocalDate orderDate;

    public Order(BigInteger orderId, BigInteger customerId, String status)
    {
        this.orderId = orderId;
        this.customerId = customerId;
        this.status=OrderStatus.valueOf(status);
    }

    public enum OrderStatus{
        Canceled,Pending,Shipped;
    }

    public Order(BigInteger orderId, BigInteger customerId, @Nullable BigInteger salesmanId, String status, LocalDate orderDate) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.salesmanId = salesmanId;
        this.status = OrderStatus.valueOf(status);

        this.orderDate = orderDate;
    }

    public Order(BigInteger customerId, String status, LocalDate orderDate) {
        this.customerId = customerId;
        this.status = OrderStatus.valueOf(status);
        this.orderDate = orderDate;
    }

    public Order(BigInteger orderId, BigInteger customerId, OrderStatus orderStatus) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.status = orderStatus;
    }

    public Order(BigInteger orderId, BigInteger customerId, BigInteger salesmanId, String status) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.salesmanId = salesmanId;
        this.status = OrderStatus.valueOf(status);
    }

    public Order(BigInteger orderId, OrderStatus orderStatus)
    {
        this.orderId = orderId;
        this.status=orderStatus;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(orderId, order.orderId) && Objects.equals(customerId, order.customerId) && Objects.equals(salesmanId, order.salesmanId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, customerId, salesmanId);
    }

}

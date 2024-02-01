package com.brandedCompany.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigInteger;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class OrderItem extends Domain {
    private BigInteger orderId, itemId, productId;
    private Double quantity, unitPrice;

    public OrderItem(BigInteger orderId,BigInteger productId, Double quantity, Double unitPrice) {
        this.orderId= orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(orderId, orderItem.orderId) && Objects.equals(itemId, orderItem.itemId) && Objects.equals(productId, orderItem.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, itemId, productId);
    }
}

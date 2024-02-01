package com.brandedCompany.domain;

import lombok.*;

import java.math.BigInteger;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Inventory extends Domain{
    private BigInteger productId,warehouseId,quantity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inventory inventory = (Inventory) o;
        return Objects.equals(productId, inventory.productId) && Objects.equals(warehouseId, inventory.warehouseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, warehouseId);
    }
}

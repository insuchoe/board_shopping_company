package com.brandedCompany.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.C;

import java.math.BigInteger;
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class InventoryDetail extends Domain{
    private String regionName,countryName,city,address,warehouseName,productName;
    private BigInteger quantity;
    private Double listPrice;
}

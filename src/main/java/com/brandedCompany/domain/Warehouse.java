package com.brandedCompany.domain;

import lombok.*;

import java.math.BigInteger;
import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
public class Warehouse extends Domain{
 private BigInteger warehouseId,locationId;
 private String warehouseName;

 @Override
 public boolean equals(Object o) {
  if (this == o) return true;
  if (o == null || getClass() != o.getClass()) return false;
  Warehouse warehouse = (Warehouse) o;
  return Objects.equals(warehouseId, warehouse.warehouseId) && Objects.equals(locationId, warehouse.locationId);
 }


 @Override
 public int hashCode() {
  return Objects.hash(warehouseId, locationId);
 }
}

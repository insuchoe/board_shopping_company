package com.brandedCompany.domain;

import com.brandedCompany.domain.searchCondition.OrderHistorySearchCondition;
import com.brandedCompany.domain.searchCondition.OrderHistorySearchCondition.OrderHistoryOrderStatus;
import lombok.*;
import org.checkerframework.checker.units.qual.C;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class OrderHistory extends Domain{
  /*  private BigInteger orderId, itemId, productId, quantity, unitPrice, customerId, salesmanId;
    private String status;
    private LocalDate orderDate;*/
  private BigInteger orderId,itemId;
  private String productName,orderer,address,salesmanName;
  private OrderHistoryOrderStatus orderStatus;
  private LocalDate orderDate;
  private BigInteger quantity;
  private Double unitPrice,totalPrice;

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    OrderHistory that = (OrderHistory) o;
    return Objects.equals(orderId, that.orderId) && Objects.equals(itemId, that.itemId) && Objects.equals(productName, that.productName) && Objects.equals(orderer, that.orderer) && Objects.equals(address, that.address) && Objects.equals(salesmanName, that.salesmanName) && orderStatus == that.orderStatus && Objects.equals(orderDate, that.orderDate) && Objects.equals(quantity, that.quantity) && Objects.equals(unitPrice, that.unitPrice) && Objects.equals(totalPrice, that.totalPrice);
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(orderId, itemId, productName, orderer, address, salesmanName, orderStatus, orderDate, quantity, unitPrice, totalPrice);
  }
}

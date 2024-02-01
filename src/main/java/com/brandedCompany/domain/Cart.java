package com.brandedCompany.domain;

import com.brandedCompany.serivce.CRUDService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Cart extends Domain {
    private BigInteger customerId, productId, quantity;
    private String productName;
    private Double unitPrice;
    private LocalDateTime includedDate;

    public Cart(BigInteger customerId, BigInteger productId)
    {
        this.customerId=customerId;
        this.productId=productId;

    }


}


package com.brandedCompany.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Component
public class Customer extends Domain {
private BigInteger customerId;
    private String name,address,website;
    private Double creditLimit;

    private String imageBase64;
    public Customer(BigInteger customerId,String name) {
        this.customerId = customerId;
        this.name = name;
    }

    public Customer(BigInteger customerId, String name, String address, String website, Double creditLimit)
    {
        this.customerId = customerId;
        this.name = name;
        this.address = address;
        this.website = website;
        this.creditLimit = creditLimit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(customerId, customer.customerId) && Objects.equals(name, customer.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, name);
    }

    @Override
    public String toString() {
        return String.format(
                "%d %s %s %s %f%n",
                customerId, name, address, website, creditLimit
        );
    }


}

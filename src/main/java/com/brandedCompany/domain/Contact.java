package com.brandedCompany.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Objects;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Contact extends Domain{

    private BigInteger contactId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private BigInteger customerId;



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact that = (Contact) o;
        return Objects.equals(contactId, that.contactId) && Objects.equals(customerId, that.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contactId, customerId);
    }

    @Override
    public String toString() {
        return String.format("contactId = %d%n customerId=%d%n firstName=%s%n lastName=%s%n email=%s%n phone=%s%n customerId=%s%n",
                contactId,customerId,firstName,lastName,email,phone,customerId);
    }
}

package com.brandedCompany.domain;

import lombok.*;

import java.math.BigInteger;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class Location extends Domain{
    private BigInteger locationId,countryId;
    private String address;
    private String postalCode;
    private String  city;
    private String state;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(locationId, location.locationId) && Objects.equals(countryId, location.countryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locationId, countryId);
    }
}


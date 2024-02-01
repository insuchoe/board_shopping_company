package com.brandedCompany.domain;

import lombok.*;

import java.math.BigInteger;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter@Setter
public class Country extends Domain{
    private BigInteger countryId,regionId;
    private String countryName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Country countries = (Country) o;
        return Objects.equals(countryId, countries.countryId) && Objects.equals(regionId, countries.regionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(countryId, regionId);
    }
}

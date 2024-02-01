package com.brandedCompany.domain;

import lombok.*;

import java.math.BigInteger;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter@Setter
public class Region extends Domain{
    private BigInteger regionId;
    private String regionName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Region regions = (Region) o;
        return Objects.equals(regionId, regions.regionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(regionId);
    }
}

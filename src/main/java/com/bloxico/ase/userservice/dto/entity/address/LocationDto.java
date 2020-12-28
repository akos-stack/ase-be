package com.bloxico.ase.userservice.dto.entity.address;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(of = {"city", "address"})
public class LocationDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("city")
    private CityDto city;

    @JsonProperty("address")
    private String address;

    @JsonProperty("latitude")
    private BigDecimal latitude;

    @JsonProperty("longitude")
    private BigDecimal longitude;

}

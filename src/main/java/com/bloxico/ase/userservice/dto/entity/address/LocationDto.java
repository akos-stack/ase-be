package com.bloxico.ase.userservice.dto.entity.address;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(of = {"city", "address"})
public class LocationDto {

    private Long id;
    private CityDto city;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;

}

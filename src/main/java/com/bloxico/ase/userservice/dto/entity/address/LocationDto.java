package com.bloxico.ase.userservice.dto.entity.address;

import com.bloxico.ase.userservice.dto.entity.BaseEntityDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(of = {"country", "address"}, callSuper = false)
public class LocationDto extends BaseEntityDto {

    @JsonProperty("country")
    private CountryDto country;

    @JsonProperty("address")
    private String address;

    @JsonProperty("address2")
    private String address2;

    @JsonProperty("city")
    private String city;

    @JsonProperty("zip_code")
    private String zipCode;

    @JsonProperty("latitude")
    private BigDecimal latitude;

    @JsonProperty("longitude")
    private BigDecimal longitude;

}

package com.bloxico.ase.userservice.dto.entity.address;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"country", "name"})
public class CityDto {

    private Integer id;
    private CountryDto country;
    private String name;
    private String zipCode;

}

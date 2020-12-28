package com.bloxico.ase.userservice.dto.entity.address;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "name")
public class CountryDto {

    private Integer id;
    private String name;

}

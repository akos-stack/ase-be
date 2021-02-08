package com.bloxico.ase.userservice.dto.projection;

import com.bloxico.ase.userservice.entity.address.Country;
import lombok.Value;

@Value
public class CountryEvaluatorsCounterDto {

    Country country;
    Long totalOfEvaluators;

}

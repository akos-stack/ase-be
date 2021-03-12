package com.bloxico.ase.userservice.dto.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class BaseEntityDto extends BaseEntityAuditDto{

    @JsonProperty("id")
    private Long id;
}

package com.bloxico.ase.userservice.dto.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BaseEntityDto extends BaseEntityAuditDto {

    @JsonProperty("id")
    private Long id;

}

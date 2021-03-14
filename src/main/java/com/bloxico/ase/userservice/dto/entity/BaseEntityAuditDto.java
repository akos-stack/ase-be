package com.bloxico.ase.userservice.dto.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BaseEntityAuditDto {

    @JsonIgnore
    private Long creatorId;

    @JsonIgnore
    private Long updaterId;

    @JsonIgnore
    private LocalDateTime createdAt;

    @JsonIgnore
    private LocalDateTime updatedAt;

    @JsonIgnore
    private long version;

}

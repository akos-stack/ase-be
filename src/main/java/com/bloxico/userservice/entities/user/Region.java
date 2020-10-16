package com.bloxico.userservice.entities.user;


import com.bloxico.userservice.entities.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Data
@EqualsAndHashCode(of = {"regionName"}, callSuper = false)
@Table(name = "regions")
public class Region extends BaseEntity {

    @Column(name = "region_name", nullable = false)
    private String regionName;
}

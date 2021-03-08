package com.bloxico.ase.userservice.entity.address;

import com.bloxico.ase.userservice.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

import java.util.HashSet;
import java.util.Set;

import static javax.persistence.FetchType.LAZY;

@Data
@EqualsAndHashCode(of = "name", callSuper = false)
@Entity
@Table(name = "countries")
public class Country extends BaseEntity {

    @Column(name = "name")
    private String name;

    @ManyToMany(fetch = LAZY)
    @JoinTable(
            name = "countries_regions",
            joinColumns = @JoinColumn(name = "country_id"),
            inverseJoinColumns = @JoinColumn(name = "region_id"))
    private Set<Region> regions = new HashSet<>();

}

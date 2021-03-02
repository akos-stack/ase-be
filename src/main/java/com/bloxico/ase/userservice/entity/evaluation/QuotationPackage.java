package com.bloxico.ase.userservice.entity.evaluation;

import com.bloxico.ase.userservice.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(of = "artworkId", callSuper = false)
@Entity
@Table(name = "quotation_packages")
public class QuotationPackage extends BaseEntity {

    @Column(name = "artwork_id")
    private Long artworkId;

}

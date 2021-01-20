package com.bloxico.ase.userservice.entity.quotationpackage;

import com.bloxico.ase.userservice.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

import java.math.BigDecimal;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@EqualsAndHashCode(of = "name", callSuper = true)
@Entity
@Table(name = "quotation_packages")
public class QuotationPackage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "image_path")
    private String imagePath;

    /*
     I don't have enough information about this field
     @Column(name = "based")
     private Based based;
    */

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "number_of_evaluations")
    private int numberOfEvaluations;

    @Column(name = "active")
    private boolean active;

}

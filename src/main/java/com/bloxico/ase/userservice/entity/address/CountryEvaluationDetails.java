package com.bloxico.ase.userservice.entity.address;

import com.bloxico.ase.userservice.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "countries_evaluation_details")
public class CountryEvaluationDetails extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @Column(name = "price_per_evaluation")
    private int pricePerEvaluation;

    @Column(name = "availability_percentage")
    private int availabilityPercentage;

    @OneToOne
    @JoinColumn(name = "country_id")
    private Country country;

}

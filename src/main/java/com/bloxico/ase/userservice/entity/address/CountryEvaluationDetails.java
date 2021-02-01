package com.bloxico.ase.userservice.entity.address;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@Table(name = "countries_evaluation_details")
public class CountryEvaluationDetails implements Serializable {

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

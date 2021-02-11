package com.bloxico.ase.userservice.repository.address;

import com.bloxico.ase.userservice.entity.address.CountryEvaluationDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountryEvaluationDetailsRepository extends JpaRepository<CountryEvaluationDetails, Integer> {

    Optional<CountryEvaluationDetails> findByCountryId(int id);

}

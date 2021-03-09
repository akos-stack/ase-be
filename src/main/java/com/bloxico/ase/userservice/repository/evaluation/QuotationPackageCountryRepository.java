package com.bloxico.ase.userservice.repository.evaluation;

import com.bloxico.ase.userservice.entity.evaluation.QuotationPackageCountry;
import com.bloxico.ase.userservice.entity.evaluation.QuotationPackageCountry.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuotationPackageCountryRepository extends JpaRepository<QuotationPackageCountry, Id> {

    Optional<QuotationPackageCountry> findByIdCountryId(Long countryId);

}

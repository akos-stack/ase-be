package com.bloxico.ase.userservice.repository.quotationpackage;

import com.bloxico.ase.userservice.entity.quotationpackage.QuotationPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuotationPackageRepository extends JpaRepository<QuotationPackage, Long> {

    Optional<QuotationPackage> findByNameIgnoreCase(String name);

}

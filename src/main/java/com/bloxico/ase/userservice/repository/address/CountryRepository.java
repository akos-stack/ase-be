package com.bloxico.ase.userservice.repository.address;

import com.bloxico.ase.userservice.entity.address.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    Optional<Country> findByNameIgnoreCase(String name);

    int countByRegionsIdEquals(Long regionId);

}

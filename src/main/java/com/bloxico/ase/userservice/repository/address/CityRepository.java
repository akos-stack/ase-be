package com.bloxico.ase.userservice.repository.address;

import com.bloxico.ase.userservice.entity.address.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Integer> {

    Optional<City> findByNameIgnoreCase(String name);

}

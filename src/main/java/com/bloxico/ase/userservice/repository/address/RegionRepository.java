package com.bloxico.ase.userservice.repository.address;

import com.bloxico.ase.userservice.entity.address.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Integer> {

    Optional<Region> findByNameIgnoreCase(String name);

}

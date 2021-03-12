package com.bloxico.ase.userservice.repository.address;

import com.bloxico.ase.userservice.entity.address.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {

    Optional<Region> findByNameIgnoreCase(String name);

    List<Region> findAllByNameInIgnoreCase(Collection<String> regionNames);

}

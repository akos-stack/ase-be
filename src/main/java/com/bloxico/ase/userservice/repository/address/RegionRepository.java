package com.bloxico.ase.userservice.repository.address;

import com.bloxico.ase.userservice.entity.address.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Long> {

    Optional<Region> findByNameIgnoreCase(String name);

    List<Region> findAllByNameInIgnoreCase(Collection<String> regionNames);

}

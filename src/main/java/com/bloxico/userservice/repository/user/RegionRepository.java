package com.bloxico.userservice.repository.user;

import com.bloxico.userservice.entities.user.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
    Optional<Region> findByRegionName(String regionName);


    List<Region> findAllByOrderByRegionNameAsc();
}

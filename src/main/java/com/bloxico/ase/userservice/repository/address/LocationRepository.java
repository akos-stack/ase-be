package com.bloxico.ase.userservice.repository.address;

import com.bloxico.ase.userservice.entity.address.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
}

package com.bloxico.ase.userservice.repository.aspiration;

import com.bloxico.ase.userservice.entity.aspiration.Aspiration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface AspirationRepository extends JpaRepository<Aspiration, Short> {

    List<Aspiration> findAllByRoleNameIn(Collection<String> names);

}

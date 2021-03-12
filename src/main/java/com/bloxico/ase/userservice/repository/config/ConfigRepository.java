package com.bloxico.ase.userservice.repository.config;

import com.bloxico.ase.userservice.entity.config.Config;
import com.bloxico.ase.userservice.entity.config.Config.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigRepository extends JpaRepository<Config, Long> {

    Optional<Config> findByType(Type type);

}

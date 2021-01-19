package com.bloxico.ase.userservice.repository.user;

import com.bloxico.ase.userservice.entity.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    @Query("SELECT u FROM User u WHERE u.enabled = FALSE AND id IN ?1")
    List<User> findAllDisabledByIds(Collection<Long> ids);

    List<User> findDistinctByEmailContainingAndRoles_NameContaining(String email, String role, Pageable pageable);

}

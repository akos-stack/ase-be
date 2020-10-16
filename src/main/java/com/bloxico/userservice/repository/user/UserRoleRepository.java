package com.bloxico.userservice.repository.user;

import com.bloxico.userservice.entities.user.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    Optional<UserRole> findByCoinUserId(long coinUserId);


    void deleteByIdIn(List<Long> ids);

    void deleteById(Long id);
}

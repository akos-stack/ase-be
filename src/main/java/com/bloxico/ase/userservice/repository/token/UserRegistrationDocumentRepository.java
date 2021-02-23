package com.bloxico.ase.userservice.repository.token;

import com.bloxico.ase.userservice.entity.token.UserRegistrationDocument;
import com.bloxico.ase.userservice.entity.token.UserRegistrationDocumentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRegistrationDocumentRepository extends JpaRepository<UserRegistrationDocument, UserRegistrationDocumentId> {
}

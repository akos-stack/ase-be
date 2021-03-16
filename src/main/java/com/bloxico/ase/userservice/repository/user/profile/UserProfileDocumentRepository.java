package com.bloxico.ase.userservice.repository.user.profile;

import com.bloxico.ase.userservice.entity.user.profile.UserProfileDocument;
import com.bloxico.ase.userservice.entity.user.profile.UserProfileDocument.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileDocumentRepository extends JpaRepository<UserProfileDocument, Id> {
}

package com.bloxico.ase.userservice.entity.user.profile;

import lombok.Data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "user_profiles_documents")
public class UserProfileDocument implements Serializable {

    @EmbeddedId
    private UserProfileDocumentId userProfileDocumentId;

}

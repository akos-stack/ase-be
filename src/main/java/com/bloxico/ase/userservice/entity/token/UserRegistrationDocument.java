package com.bloxico.ase.userservice.entity.token;

import lombok.Data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "user_profile_image_document")
public class UserRegistrationDocument implements Serializable {

    @EmbeddedId
    private UserRegistrationDocumentId userRegistrationDocumentId;

}

package com.bloxico.ase.userservice.entity.document;

import com.bloxico.ase.userservice.entity.BaseEntity;
import com.bloxico.ase.userservice.util.FileCategory;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@Entity
@Table(name = "documents")
public class Document extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "path")
    private String path;

    @Column(name = "type")
    @Enumerated(STRING)
    private FileCategory type;
}

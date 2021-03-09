package com.bloxico.ase.userservice.entity.document;

import com.bloxico.ase.userservice.entity.BaseEntity;
import com.bloxico.ase.userservice.util.FileCategory;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import static javax.persistence.EnumType.STRING;

@Data
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@Entity
@Table(name = "documents")
public class Document extends BaseEntity {

    @Column(name = "path")
    private String path;

    @Column(name = "type")
    @Enumerated(STRING)
    private FileCategory type;
}

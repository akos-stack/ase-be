package com.bloxico.ase.userservice.entity.artwork.metadata;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "styles")
public class Style extends ArtworkMetadata {
}
package com.bloxico.ase.userservice.entity.artwork;

public interface IArtworkMetadataEntity {

    Integer getId();

    void setId(Integer id);

    String getName();

    void setName(String name);

    ArtworkMetadataStatus getStatus();

    void setStatus(ArtworkMetadataStatus status);

    Long getUpdaterId();

    void setUpdaterId(Long updaterId);

    Long getCreatorId();

    void setCreatorId(Long updaterId);
}

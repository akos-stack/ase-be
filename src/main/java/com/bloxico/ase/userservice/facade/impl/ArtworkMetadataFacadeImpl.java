package com.bloxico.ase.userservice.facade.impl;

import com.bloxico.ase.userservice.dto.entity.artwork.metadata.ArtworkMetadataDto;
import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Status;
import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata.Type;
import com.bloxico.ase.userservice.facade.IArtworkMetadataFacade;
import com.bloxico.ase.userservice.service.artwork.IArtworkMetadataService;
import com.bloxico.ase.userservice.service.artwork.impl.metadata.*;
import com.bloxico.ase.userservice.web.model.artwork.metadata.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Artwork.ARTWORK_METADATA_TYPE_NOT_FOUND;

@Slf4j
@Service
@Transactional
public class ArtworkMetadataFacadeImpl implements IArtworkMetadataFacade {

    private final CategoryServiceImpl categoryMetadataService;
    private final MaterialServiceImpl materialMetadataService;
    private final MediumServiceImpl mediumMetadataService;
    private final StyleServiceImpl styleMetadataService;

    @Autowired
    public ArtworkMetadataFacadeImpl(CategoryServiceImpl categoryMetadataService,
                                     MaterialServiceImpl materialMetadataService,
                                     MediumServiceImpl mediumMetadataService,
                                     StyleServiceImpl styleMetadataService)
    {
        this.categoryMetadataService = categoryMetadataService;
        this.materialMetadataService = materialMetadataService;
        this.mediumMetadataService = mediumMetadataService;
        this.styleMetadataService = styleMetadataService;
    }

    @Override
    public ArtworkMetadataDto saveArtworkMetadata(SaveArtworkMetadataRequest request) {
        log.info("AbstractArtworkMetadataFacadeImpl.saveArtworkMetadata - start | request: {}", request);
        var dto = MAPPER.toArtworkMetadataDto(request);
        var response = getService(request.getType()).findOrSaveArtworkMetadata(dto);
        log.info("AbstractArtworkMetadataFacadeImpl.saveArtworkMetadata - end | request: {}", request);
        return response;
    }

    @Override
    public ArtworkMetadataDto updateArtworkMetadata(UpdateArtworkMetadataRequest request) {
        log.info("AbstractArtworkMetadataFacadeImpl.updateArtworkMetadata - start | request: {}", request);
        var dto = MAPPER.toArtworkMetadataDto(request);
        var updated = getService(request.getType()).updateArtworkMetadata(dto);
        log.info("AbstractArtworkMetadataFacadeImpl.updateArtworkMetadata - end | request: {}", request);
        return updated;
    }

    @Override
    public void deleteArtworkMetadata(String name, Type type) {
        log.info("AbstractArtworkMetadataFacadeImpl.deleteArtworkMetadata - start | name: {}, type: {}", name, type);
        getService(type).deleteArtworkMetadata(name);
        log.info("AbstractArtworkMetadataFacadeImpl.deleteArtworkMetadata - end | name: {}, type: {}", name, type);
    }

    @Override
    public PagedArtworkMetadataResponse searchArtworkMetadata(Type type, Status status, String name, int page, int size, String sort) {
        log.info("AbstractArtworkMetadataFacadeImpl.searchArtworkMetadata - start | type: {}, status: {}, page: {}, size: {}, sort: {}", type, status, page, size, sort);
        var pagedDto = getService(type).searchArtworkMetadata(status, name, page, size, sort);
        var response = new PagedArtworkMetadataResponse(pagedDto.getContent(), pagedDto.getContent().size(), pagedDto.getTotalElements(), pagedDto.getTotalPages());
        log.info("AbstractArtworkMetadataFacadeImpl.searchArtworkMetadata - end | type: {}, status: {}, page: {}, size: {}, sort: {}", type, status, page, size, sort);
        return response;
    }

    @Override
    public SearchArtworkMetadataResponse searchApprovedArtworkMetadata(String name, Type type) {
        log.info("AbstractArtworkMetadataFacadeImpl.searchApprovedArtworkMetadata - start | name: {}, type: {}", name, type);
        var artworkMetadataDtos = getService(type).searchApprovedArtworkMetadata(name);
        var response = new SearchArtworkMetadataResponse(artworkMetadataDtos);
        log.info("AbstractArtworkMetadataFacadeImpl.searchApprovedArtworkMetadata - end | name: {}, type: {}", name, type);
        return response;
    }

    private IArtworkMetadataService getService(Type type) {
        switch (type) {
            // @formatter:off
            case CATEGORY : return categoryMetadataService;
            case MATERIAL : return materialMetadataService;
            case MEDIUM   : return mediumMetadataService;
            case STYLE    : return styleMetadataService;
            default       : throw ARTWORK_METADATA_TYPE_NOT_FOUND.newException();
            // @formatter:on
        }
    }

}

package com.bloxico.ase.userservice.util;

import com.bloxico.ase.userservice.dto.entity.address.*;
import com.bloxico.ase.userservice.dto.entity.artwork.*;
import com.bloxico.ase.userservice.dto.entity.artwork.metadata.ArtworkMetadataDto;
import com.bloxico.ase.userservice.dto.entity.config.ConfigDto;
import com.bloxico.ase.userservice.dto.entity.document.DocumentDto;
import com.bloxico.ase.userservice.dto.entity.evaluation.*;
import com.bloxico.ase.userservice.dto.entity.oauth.OAuthAccessTokenDto;
import com.bloxico.ase.userservice.dto.entity.token.*;
import com.bloxico.ase.userservice.dto.entity.user.*;
import com.bloxico.ase.userservice.dto.entity.user.profile.*;
import com.bloxico.ase.userservice.entity.address.*;
import com.bloxico.ase.userservice.entity.artwork.*;
import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata;
import com.bloxico.ase.userservice.entity.config.Config;
import com.bloxico.ase.userservice.entity.document.Document;
import com.bloxico.ase.userservice.entity.evaluation.*;
import com.bloxico.ase.userservice.entity.oauth.OAuthAccessToken;
import com.bloxico.ase.userservice.entity.token.*;
import com.bloxico.ase.userservice.entity.user.*;
import com.bloxico.ase.userservice.entity.user.profile.*;
import com.bloxico.ase.userservice.proj.evaluation.CountryEvaluationDetailsCountedTransferProj;
import com.bloxico.ase.userservice.proj.evaluation.CountryEvaluationDetailsWithEvaluatorsCountProj;
import com.bloxico.ase.userservice.web.model.address.*;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkDataRequest;
import com.bloxico.ase.userservice.web.model.artwork.metadata.IArtworkMetadataRequest;
import com.bloxico.ase.userservice.web.model.config.SaveConfigRequest;
import com.bloxico.ase.userservice.web.model.evaluation.*;
import com.bloxico.ase.userservice.web.model.registration.RegistrationRequest;
import com.bloxico.ase.userservice.web.model.token.IPendingEvaluatorRequest;
import com.bloxico.ase.userservice.web.model.user.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper
public interface AseMapper {

    AseMapper MAPPER = Mappers.getMapper(AseMapper.class);

    // ENTITY -> DTO

    UserDto toDto(User user);

    PermissionDto toDto(Permission entity);

    RoleDto toDto(Role entity);

    RegionDto toDto(Region entity);

    CountryDto toDto(Country entity);

    LocationDto toDto(Location entity);

    UserProfileDto toDto(UserProfile entity);

    EvaluatorDto toDto(Evaluator entity);

    ArtOwnerDto toDto(ArtOwner entity);

    TokenDto toDto(Token entity);

    OAuthAccessTokenDto toDto(OAuthAccessToken entity);

    PendingEvaluatorDto toDto(PendingEvaluator entity);

    ArtworkMetadataDto toDto(ArtworkMetadata entity);

    CountryEvaluationDetailsDto toDto(CountryEvaluationDetails entity);

    QuotationPackageDto toDto(QuotationPackage entity);

    @Mapping(target = "quotationPackageId", source = "id.quotationPackageId")
    @Mapping(target = "countryId", source = "id.countryId")
    QuotationPackageCountryDto toDto(QuotationPackageCountry entity);

    DocumentDto toDto(Document document);

    ArtistDto toDto(Artist artist);

    @Mapping(target = "location.id", source = "locationId")
    ArtworkDto toDto(Artwork artwork);

    ArtworkHistoryDto toDto(ArtworkHistory entity);

    @Mapping(target = "email", source = "id.email")
    @Mapping(target = "documentId", source = "id.documentId")
    PendingEvaluatorDocumentDto toDto(PendingEvaluatorDocument entity);

    ConfigDto toDto(Config entity);

    ArtworkDocumentDto toDto(ArtworkDocument entity);

    // DTO -> ENTITY

    User toEntity(UserDto dto);

    Country toEntity(CountryDto dto);

    Location toEntity(LocationDto dto);

    UserProfile toEntity(UserProfileDto dto);

    Evaluator toEntity(EvaluatorDto dto);

    ArtOwner toEntity(ArtOwnerDto dto);

    Region toEntity(RegionDto dto);

    CountryEvaluationDetails toEntity(CountryEvaluationDetailsDto dto);

    QuotationPackage toEntity(QuotationPackageDto dto);

    @Mapping(target = "id.quotationPackageId", source = "quotationPackageId")
    @Mapping(target = "id.countryId", source = "countryId")
    QuotationPackageCountry toEntity(QuotationPackageCountryDto dto);

    @Mapping(target = "id.email", source = "email")
    @Mapping(target = "id.documentId", source = "documentId")
    PendingEvaluatorDocument toEntity(PendingEvaluatorDocumentDto dto);

    Document toEntity(DocumentDto dto);

    PendingEvaluator toEntity(PendingEvaluatorDto dto);

    Artist toEntity(ArtistDto dto);

    @Mapping(source = "dto.location.id", target = "locationId")
    Artwork toEntity(ArtworkDto dto);

    ArtworkHistory toEntity(ArtworkHistoryDto dto);

    ArtworkDocument toEntity(ArtworkDocumentDto dto);

    Config toEntity(ConfigDto dto);

    // OTHER

    @Mapping(target = "name", source = "username")
    UserDto toUserDto(RegistrationRequest request);

    @Mapping(target = "name", source = "username")
    UserDto toUserDto(ISubmitUserProfileRequest request);

    @Mapping(target = "value", source = "tokenId")
    @Mapping(target = "expiryDate", source = "expiration")
    BlacklistedToken toBlacklistedToken(OAuthAccessTokenDto token);

    @Mapping(target = "name", source = "country")
    CountryDto toCountryDto(ISubmitUserProfileRequest request);

    @Mapping(ignore = true, target = "country")
    LocationDto toLocationDto(ISubmitUserProfileRequest request);

    @Mapping(ignore = true, target = "country")
    LocationDto toLocationDto(SaveArtworkDataRequest request);

    UserProfileDto toUserProfileDto(ISubmitUserProfileRequest request);

    EvaluatorDto toEvaluatorDto(SubmitEvaluatorRequest request);

    ArtOwnerDto toArtOwnerDto(SubmitArtOwnerRequest request);

    ArtworkMetadataDto toArtworkMetadataDto(IArtworkMetadataRequest request);

    @Mapping(target = "name", source = "region")
    RegionDto toRegionDto(SaveRegionRequest request);

    RegionDto toRegionDto(SaveCountryRequest request);

    @Mapping(target = "name", source = "country")
    @Mapping(ignore = true, target = "regions")
    CountryDto toCountryDto(SaveCountryRequest request);

    @Mapping(target = "name", source = "country")
    @Mapping(ignore = true, target = "regions")
    CountryDto toCountryDto(UpdateCountryRequest request);

    CountryEvaluationDetailsDto toCountryEvaluationDetailsDto(SaveCountryEvaluationDetailsRequest request);

    CountryEvaluationDetailsDto toCountryEvaluationDetailsDto(UpdateCountryEvaluationDetailsRequest request);

    @Mapping(target = "regions", source = "country.regions")
    @Mapping(target = "country", source = "countryName")
    CountryEvaluationDetailsWithEvaluatorsCountProj toCountedProj(CountryEvaluationDetailsCountedTransferProj transferProj);

    default String toString(Region region) {
        return region.getName();
    }

    QuotationPackageDto toQuotationPackageDto(SaveQuotationPackageRequest request);

    PendingEvaluatorDto toPendingEvaluatorDto(IPendingEvaluatorRequest request);

    ArtworkHistoryDto toArtworkHistoryDto(SaveArtworkDataRequest request);

    default ConfigDto toDto(SaveConfigRequest request) {
        var dto = new ConfigDto();
        dto.setType(request.getType());
        dto.setValue(request.getValue().toString());
        return dto;
    }

    @Mapping(ignore = true, target = "artist")
    @Mapping(ignore = true, target = "ownerId")
    @Mapping(ignore = true, target = "location")
    @Mapping(ignore = true, target = "artworkHistory")
    @Mapping(ignore = true, target = "categories")
    @Mapping(ignore = true, target = "materials")
    @Mapping(ignore = true, target = "mediums")
    @Mapping(ignore = true, target = "styles")
    @Mapping(source = "artworkId", target = "id")
    ArtworkDto toArtworkDto(SaveArtworkDataRequest request);

    @Mapping(source = "artworkDto.id", target = "id")
    @Mapping(source = "artworkDto.creatorId", target = "creatorId")
    @Mapping(source = "artworkDto.updaterId", target = "updaterId")
    @Mapping(source = "artworkDto.createdAt", target = "createdAt")
    @Mapping(source = "artworkDto.updatedAt", target = "updatedAt")
    @Mapping(source = "artworkDto.version", target = "version")
    @Mapping(source = "locationDto", target = "location")
    @Mapping(source = "documents", target = "documents")
    ArtworkDto toArtworkDto(ArtworkDto artworkDto, LocationDto locationDto, Set<DocumentDto> documents);

}

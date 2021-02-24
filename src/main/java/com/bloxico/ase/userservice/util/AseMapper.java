package com.bloxico.ase.userservice.util;

import com.bloxico.ase.userservice.dto.entity.address.*;
import com.bloxico.ase.userservice.dto.entity.artwork.*;
import com.bloxico.ase.userservice.dto.entity.artwork.metadata.ArtworkMetadataDto;
import com.bloxico.ase.userservice.dto.entity.document.DocumentDto;
import com.bloxico.ase.userservice.dto.entity.evaluation.*;
import com.bloxico.ase.userservice.dto.entity.oauth.OAuthAccessTokenDto;
import com.bloxico.ase.userservice.dto.entity.token.*;
import com.bloxico.ase.userservice.dto.entity.user.RoleDto;
import com.bloxico.ase.userservice.dto.entity.user.UserDto;
import com.bloxico.ase.userservice.dto.entity.user.profile.*;
import com.bloxico.ase.userservice.entity.address.*;
import com.bloxico.ase.userservice.entity.artwork.*;
import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata;
import com.bloxico.ase.userservice.entity.document.Document;
import com.bloxico.ase.userservice.entity.evaluation.*;
import com.bloxico.ase.userservice.entity.oauth.OAuthAccessToken;
import com.bloxico.ase.userservice.entity.token.*;
import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.entity.user.User;
import com.bloxico.ase.userservice.entity.user.profile.*;
import com.bloxico.ase.userservice.web.model.address.SaveCountryRequest;
import com.bloxico.ase.userservice.web.model.address.SaveRegionRequest;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkRequest;
import com.bloxico.ase.userservice.web.model.artwork.metadata.IArtworkMetadataRequest;
import com.bloxico.ase.userservice.web.model.evaluation.SaveCountryEvaluationDetailsRequest;
import com.bloxico.ase.userservice.web.model.evaluation.SaveQuotationPackageRequest;
import com.bloxico.ase.userservice.web.model.registration.RegistrationRequest;
import com.bloxico.ase.userservice.web.model.token.IPendingEvaluatorRequest;
import com.bloxico.ase.userservice.web.model.user.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AseMapper {

    AseMapper MAPPER = Mappers.getMapper(AseMapper.class);

    // ENTITY -> DTO

    UserDto toDto(User user);

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

    ArtworkDto toDto(Artwork artwork);

    ArtworkGroupDto toDto(ArtworkGroup artworkGroup);

    ArtworkHistoryDto toDto(ArtworkHistory artworkHistory);

    @Mapping(target = "email", source = "pendingEvaluatorDocument.pendingEvaluatorDocumentId.email")
    @Mapping(target = "documentId", source = "pendingEvaluatorDocument.pendingEvaluatorDocumentId.documentId")
    PendingEvaluatorDocumentDto toDto(PendingEvaluatorDocument pendingEvaluatorDocument);

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

    Document toEntity(DocumentDto dto);

    PendingEvaluator toEntity(PendingEvaluatorDto pendingEvaluatorDto);

    Artist toEntity(ArtistDto dto);

    Artwork toEntity(ArtworkDto dto);

    ArtworkGroup toEntity(ArtworkGroupDto dto);

    ArtworkHistory toEntity(ArtworkHistoryDto dto);

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
    LocationDto toLocationDto(SaveArtworkRequest request);

    UserProfileDto toUserProfileDto(ISubmitUserProfileRequest request);

    EvaluatorDto toEvaluatorDto(SubmitEvaluatorRequest request);

    ArtOwnerDto toArtOwnerDto(SubmitArtOwnerRequest request);

    ArtworkMetadataDto toArtworkMetadataDto(IArtworkMetadataRequest request);

    @Mapping(target = "name", source = "region")
    RegionDto toRegionDto(SaveRegionRequest request);

    @Mapping(target = "name", source = "country")
    @Mapping(ignore = true, target = "region")
    CountryDto toCountryDto(SaveCountryRequest request);

    CountryEvaluationDetailsDto toCountryEvaluationDetailsDto(SaveCountryEvaluationDetailsRequest request);

    QuotationPackageDto toQuotationPackageDto(SaveQuotationPackageRequest request);

    PendingEvaluatorDto toPendingEvaluatorDto(IPendingEvaluatorRequest request);

    @Mapping(ignore = true, target = "artist")
    @Mapping(ignore = true, target = "owner")
    @Mapping(ignore = true, target = "location")
    @Mapping(ignore = true, target = "group")
    @Mapping(ignore = true, target = "history")
    @Mapping(ignore = true, target = "categories")
    @Mapping(ignore = true, target = "materials")
    @Mapping(ignore = true, target = "mediums")
    @Mapping(ignore = true, target = "styles")
    ArtworkDto toArtworkDto(SaveArtworkRequest request);

    ArtworkHistoryDto toArtworkHistoryDto(SaveArtworkRequest request);

    @Mapping(source = "groupId", target = "id")
    ArtworkGroupDto toArtworkGroupDto(SaveArtworkRequest request);

}

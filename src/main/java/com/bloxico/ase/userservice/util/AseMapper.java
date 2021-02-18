package com.bloxico.ase.userservice.util;

import com.bloxico.ase.userservice.dto.entity.address.CountryDto;
import com.bloxico.ase.userservice.dto.entity.address.LocationDto;
import com.bloxico.ase.userservice.dto.entity.address.RegionDto;
import com.bloxico.ase.userservice.dto.entity.artwork.*;
import com.bloxico.ase.userservice.dto.entity.document.DocumentDto;
import com.bloxico.ase.userservice.dto.entity.evaluation.CountryEvaluationDetailsDto;
import com.bloxico.ase.userservice.dto.entity.oauth.OAuthAccessTokenDto;
import com.bloxico.ase.userservice.dto.entity.token.PendingEvaluatorDocumentDto;
import com.bloxico.ase.userservice.dto.entity.token.PendingEvaluatorDto;
import com.bloxico.ase.userservice.dto.entity.token.TokenDto;
import com.bloxico.ase.userservice.dto.entity.user.RoleDto;
import com.bloxico.ase.userservice.dto.entity.user.UserDto;
import com.bloxico.ase.userservice.dto.entity.user.profile.ArtOwnerDto;
import com.bloxico.ase.userservice.dto.entity.user.profile.EvaluatorDto;
import com.bloxico.ase.userservice.dto.entity.user.profile.UserProfileDto;
import com.bloxico.ase.userservice.entity.address.Country;
import com.bloxico.ase.userservice.entity.address.Location;
import com.bloxico.ase.userservice.entity.address.Region;
import com.bloxico.ase.userservice.entity.artwork.Artist;
import com.bloxico.ase.userservice.entity.artwork.Artwork;
import com.bloxico.ase.userservice.entity.artwork.ArtworkGroup;
import com.bloxico.ase.userservice.entity.artwork.ArtworkHistory;
import com.bloxico.ase.userservice.entity.artwork.metadata.ArtworkMetadata;
import com.bloxico.ase.userservice.entity.document.Document;
import com.bloxico.ase.userservice.entity.evaluation.CountryEvaluationDetails;
import com.bloxico.ase.userservice.entity.oauth.OAuthAccessToken;
import com.bloxico.ase.userservice.entity.token.BlacklistedToken;
import com.bloxico.ase.userservice.entity.token.PendingEvaluator;
import com.bloxico.ase.userservice.entity.token.PendingEvaluatorDocument;
import com.bloxico.ase.userservice.entity.token.Token;
import com.bloxico.ase.userservice.entity.user.Role;
import com.bloxico.ase.userservice.entity.user.User;
import com.bloxico.ase.userservice.entity.user.profile.ArtOwner;
import com.bloxico.ase.userservice.entity.user.profile.Evaluator;
import com.bloxico.ase.userservice.entity.user.profile.UserProfile;
import com.bloxico.ase.userservice.web.model.address.SaveCountryRequest;
import com.bloxico.ase.userservice.web.model.address.SaveRegionRequest;
import com.bloxico.ase.userservice.web.model.artwork.IArtworkMetadataRequest;
import com.bloxico.ase.userservice.web.model.artwork.SubmitArtworkRequest;
import com.bloxico.ase.userservice.web.model.evaluation.SaveCountryEvaluationDetailsRequest;
import com.bloxico.ase.userservice.web.model.registration.RegistrationRequest;
import com.bloxico.ase.userservice.web.model.token.IPendingEvaluatorRequest;
import com.bloxico.ase.userservice.web.model.user.ISubmitUserProfileRequest;
import com.bloxico.ase.userservice.web.model.user.SubmitArtOwnerRequest;
import com.bloxico.ase.userservice.web.model.user.SubmitEvaluatorRequest;
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
    LocationDto toLocationDto(SubmitArtworkRequest request);

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
    ArtworkDto toArtworkDto(SubmitArtworkRequest request);
}

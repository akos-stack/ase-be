package com.bloxico.ase.userservice.web.model.artwork;

import com.bloxico.ase.userservice.util.FileCategory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.bloxico.ase.userservice.facade.impl.ArtworkDocumentsFacadeImpl.SINGLETONS;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Artwork.ARTWORK_ONLY_ONE_DOCUMENT_ALLOWED_FOR_CATEGORY;
import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class UploadArtworkDocumentsRequest {

    @NotNull
    @JsonProperty("artwork_id")
    @ApiModelProperty(required = true)
    Long artworkId;

    @NotNull
    @NotEmpty
    @JsonProperty("documents")
    @ApiModelProperty(required = true)
    List<MultipartFile> documents;

    @JsonProperty("file_category")
    @ApiModelProperty(required = true)
    FileCategory fileCategory;

    @JsonIgnore
    public void validateSingletonDocuments() {
        //noinspection ConstantConditions
        if (SINGLETONS.contains(fileCategory) && documents.size() > 1)
            throw ARTWORK_ONLY_ONE_DOCUMENT_ALLOWED_FOR_CATEGORY.newException();
    }

}

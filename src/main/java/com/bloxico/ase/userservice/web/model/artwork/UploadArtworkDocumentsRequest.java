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
import java.util.Set;

import static com.bloxico.ase.userservice.util.FileCategory.*;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.Artwork.ARTWORK_ONLY_ONE_DOCUMENT_ALLOWED_FOR_CATEGORY;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadArtworkDocumentsRequest {

    @JsonIgnore
    public static final Set<FileCategory> SINGLETONS = Set.of(CERTIFICATE, CV, PRINCIPAL_IMAGE);

    @NotNull
    @JsonProperty("artwork_id")
    @ApiModelProperty(required = true)
    private Long artworkId;

    @NotNull
    @NotEmpty
    @JsonProperty("documents")
    @ApiModelProperty(required = true)
    private List<MultipartFile> documents;

    @JsonProperty("file_category")
    @ApiModelProperty(required = true)
    private FileCategory fileCategory;

    @JsonIgnore
    public void validateSingletonDocuments() {
        if (SINGLETONS.contains(fileCategory) && documents.size() > 1)
            throw ARTWORK_ONLY_ONE_DOCUMENT_ALLOWED_FOR_CATEGORY.newException();
    }

}

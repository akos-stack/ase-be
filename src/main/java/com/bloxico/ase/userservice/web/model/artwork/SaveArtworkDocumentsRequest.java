package com.bloxico.ase.userservice.web.model.artwork;

import com.bloxico.ase.userservice.util.FileCategory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.bloxico.ase.userservice.web.error.ErrorCodes.Artwork.ARTWORK_ONLY_ONE_DOCUMENT_ALLOWED_FOR_CATEGORY;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveArtworkDocumentsRequest {

    @NotNull
    @NotEmpty
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
    public void validateDocuments() {
        if((FileCategory.CV == fileCategory || FileCategory.CERTIFICATE == fileCategory || FileCategory.PRINCIPAL_IMAGE == fileCategory)
                && documents.size() > 1) {
            throw ARTWORK_ONLY_ONE_DOCUMENT_ALLOWED_FOR_CATEGORY.newException();
        }
    }
}

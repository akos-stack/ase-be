package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.WithOwner;
import com.bloxico.ase.userservice.web.model.artwork.*;
import com.bloxico.ase.userservice.web.model.artwork.metadata.SetArtworkPrincipalImageRequest;
import org.springframework.core.io.ByteArrayResource;

public interface IArtworkDocumentsFacade {

    ByteArrayResource downloadArtworkDocument(ArtworkDocumentRequest request);

    UploadArtworkDocumentsResponse uploadArtworkDocuments(WithOwner<UploadArtworkDocumentsRequest> withOwner);

    void deleteArtworkDocument(WithOwner<ArtworkDocumentRequest> withOwner);

    void setPrincipalImage(WithOwner<SetArtworkPrincipalImageRequest>  request);
}

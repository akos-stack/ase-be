package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.artwork.ArtworkDocumentRequest;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkDocumentsRequest;
import com.bloxico.ase.userservice.web.model.artwork.SaveArtworkResponse;
import org.springframework.core.io.ByteArrayResource;

public interface IArtworkDocumentsFacade {

    SaveArtworkResponse saveArtworkDocuments(SaveArtworkDocumentsRequest request);

    ByteArrayResource downloadArtworkDocument(ArtworkDocumentRequest request);

    SaveArtworkResponse deleteArtworkDocument(ArtworkDocumentRequest request);
}

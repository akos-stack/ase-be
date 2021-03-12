package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.s3.*;
import org.springframework.core.io.ByteArrayResource;

public interface IS3Facade {

    void validateFile(ValidateFileRequest request);

    ByteArrayResource downloadFile(DownloadFileRequest request);

    void deleteFile(DeleteFileRequest request);

}

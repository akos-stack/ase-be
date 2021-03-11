package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.web.model.aws.ValidateFilesRequest;
import com.bloxico.ase.userservice.web.model.aws.ValidateFilesResponse;
import org.springframework.core.io.ByteArrayResource;

public interface IS3Facade {

    ValidateFilesResponse invalidFiles(ValidateFilesRequest validateFilesRequest);

    ByteArrayResource downloadFile(String path);

    void deleteFile(String path);

}

package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.util.FileCategory;
import com.bloxico.ase.userservice.web.model.aws.ValidateFilesRequest;
import com.bloxico.ase.userservice.web.model.aws.ValidateFilesResponse;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

public interface IS3Facade {

    void validateFile(FileCategory category, MultipartFile file);

    ValidateFilesResponse invalidFiles(ValidateFilesRequest validateFilesRequest);

    ByteArrayResource downloadFile(String path);

    void deleteFile(String path);

}

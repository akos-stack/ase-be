package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.util.FileCategory;
import com.bloxico.ase.userservice.web.model.aws.ValidateFilesRequest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IS3Facade {

    void validateFile(FileCategory category, MultipartFile file);

    List<String> validateFiles(ValidateFilesRequest validateFilesRequest);

    ByteArrayResource downloadFile(String path);

    void deleteFile(String path);

}

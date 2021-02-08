package com.bloxico.ase.userservice.service.aws;

import com.bloxico.ase.userservice.util.FileCategory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

public interface IS3Service {

    void validateFile(FileCategory category, MultipartFile file);

    String uploadFile(FileCategory category, MultipartFile file);

    ByteArrayResource downloadFile(String path);

    void deleteFile(String path);

}

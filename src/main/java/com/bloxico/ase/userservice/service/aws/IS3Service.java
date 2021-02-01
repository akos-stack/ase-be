package com.bloxico.ase.userservice.service.aws;

import com.bloxico.ase.userservice.util.FileCategory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

public interface IS3Service {

    void validateFile(FileCategory fileCategory, MultipartFile file);

    String uploadFile(FileCategory fileCategory, MultipartFile file);

    ByteArrayResource downloadFile(String fileName);

    boolean deleteFile(String fileName);

}

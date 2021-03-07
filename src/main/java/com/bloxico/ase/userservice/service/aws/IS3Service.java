package com.bloxico.ase.userservice.service.aws;

import com.bloxico.ase.userservice.util.FileCategory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IS3Service {

    void validateFile(FileCategory category, MultipartFile file);

    List<String> validateFiles(FileCategory category, List<MultipartFile> files);

    String uploadFile(FileCategory category, MultipartFile file);

    ByteArrayResource downloadFile(String path);

    void deleteFile(String path);

}

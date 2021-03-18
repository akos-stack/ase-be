package com.bloxico.ase.userservice.service.aws;

import com.bloxico.ase.userservice.util.FileCategory;
import com.bloxico.ase.userservice.web.error.ErrorCodes;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

public interface IS3Service {

    Map<String, Set<ErrorCodes.AmazonS3>> validateFiles(FileCategory category, List<MultipartFile> files);

    void validateFile(FileCategory category, MultipartFile file);

    String uploadFile(FileCategory category, MultipartFile file);

    ByteArrayResource downloadFile(String path);

    void deleteFile(String path);

}

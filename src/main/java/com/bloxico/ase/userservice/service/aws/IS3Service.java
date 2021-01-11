package com.bloxico.ase.userservice.service.aws;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

public interface IS3Service {

    String uploadFile(MultipartFile file);

    ByteArrayResource downloadFile(String fileName);

    boolean deleteFile(String fileName);
}

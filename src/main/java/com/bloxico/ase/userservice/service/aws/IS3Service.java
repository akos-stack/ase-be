package com.bloxico.ase.userservice.service.aws;

import org.springframework.web.multipart.MultipartFile;

public interface IS3Service {

    String uploadFile(MultipartFile file);

    boolean deleteFile(String fileName);
}

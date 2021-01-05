package com.bloxico.ase.userservice.facade;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

public interface IS3Facade {

    void uploadFile(MultipartFile file);

    ByteArrayResource downloadFile(String fileName);

    void deleteFile(String fileName);
}

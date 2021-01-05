package com.bloxico.ase.userservice.facade;

import org.springframework.web.multipart.MultipartFile;

public interface IS3Facade {

    void uploadFile(MultipartFile file);

    void deleteFile(String fileName);
}

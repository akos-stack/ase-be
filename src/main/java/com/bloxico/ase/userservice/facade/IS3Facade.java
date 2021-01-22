package com.bloxico.ase.userservice.facade;

import com.bloxico.ase.userservice.util.FileCategory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

public interface IS3Facade {

    boolean validateFile(FileCategory fileCategory, MultipartFile file);

    ByteArrayResource downloadFile(String fileName);

    void deleteFile(String fileName);
}

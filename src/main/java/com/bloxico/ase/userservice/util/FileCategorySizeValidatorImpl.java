package com.bloxico.ase.userservice.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileCategorySizeValidatorImpl {

    @Value("${upload.file.max-cv-size}")
    private long cvMaxFileSize;

    @Value("${upload.file.max-image-size}")
    private long imageMaxFileSize;

    public boolean validateCV(Long fileSize) {
        return fileSize > this.cvMaxFileSize;
    }

    public boolean validateImage(Long fileSize) {
        return fileSize > this.imageMaxFileSize;
    }
}

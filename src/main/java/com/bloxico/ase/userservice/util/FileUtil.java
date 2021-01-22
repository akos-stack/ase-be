package com.bloxico.ase.userservice.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import static java.util.Objects.requireNonNull;

@Component
public class FileUtil {

    @Autowired
    FileCategorySizeValidatorImpl fileCategorySizeValidator;

    public boolean validate(FileCategory fileCategory, MultipartFile file) {
        requireNonNull(file);
        requireNonNull(fileCategory);
        fileCategory.validate(file, fileCategorySizeValidator);
        return true;
    }

    public static long toKiloBytes(long bytes) {
        return bytes/1024l;
    }
}

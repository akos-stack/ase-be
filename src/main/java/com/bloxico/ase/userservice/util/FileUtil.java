package com.bloxico.ase.userservice.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import static com.bloxico.ase.userservice.web.error.ErrorCodes.AmazonS3.FILE_SIZE_EXCEEDED;
import static java.util.Objects.requireNonNull;

@Component
public class FileUtil {

    @Autowired
    Environment environment;

    public void validate(FileCategory fileCategory, MultipartFile file) {
        requireNonNull(file);
        requireNonNull(fileCategory);
        fileCategory.validate(file, environment);
    }

    public static long toKiloBytes(long bytes) {
        return bytes/1024;
    }

    public static void validateFileSize(long fileSize, long maxSize) {
        if(toKiloBytes(fileSize) > maxSize)
            throw FILE_SIZE_EXCEEDED.newException();
    }
}

package com.bloxico.ase.userservice.util;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.env.Environment;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.UUID;

import static com.bloxico.ase.userservice.web.error.ErrorCodes.AmazonS3.FILE_TYPE_NOT_SUPPORTED_FOR_CATEGORY;
import static java.util.Objects.requireNonNull;

public enum FileCategory {
    CV (SupportedFileExtension.pdf, SupportedFileExtension.doc, SupportedFileExtension.txt) {

        @Override
        public void validate(MultipartFile file, Environment environment) {
            if(!CV.supportedTypes.contains(SupportedFileExtension.getByExtension(FilenameUtils.getExtension(file.getOriginalFilename()))))
                throw FILE_TYPE_NOT_SUPPORTED_FOR_CATEGORY.newException();
            long maxFileSize = Long.parseLong(requireNonNull(environment.getProperty("upload.file.max-cv-size")));
            FileUtil.validateFileSize(file.getSize(), maxFileSize);
        }

        @Override
        public String generateFilePath(Environment environment) {
            return environment.getProperty("upload.cv.file-path");
        }
    },
    IMAGE (SupportedFileExtension.jpg, SupportedFileExtension.png) {

        @Override
        public void validate(MultipartFile file, Environment environment) {
            if(!IMAGE.supportedTypes.contains(SupportedFileExtension.getByExtension(FilenameUtils.getExtension(file.getOriginalFilename()))))
                throw FILE_TYPE_NOT_SUPPORTED_FOR_CATEGORY.newException();
            long maxFileSize = Long.parseLong(requireNonNull(environment.getProperty("upload.file.max-image-size")));
            FileUtil.validateFileSize(file.getSize(), maxFileSize);
        }

        @Override
        public String generateFilePath(Environment environment) {
            return environment.getProperty("upload.image.file-path") + UUID.randomUUID().toString() + "/";
        }
    };

    public abstract void validate(MultipartFile file, Environment environment);

    public abstract String generateFilePath(Environment environment);

    private final Set<SupportedFileExtension> supportedTypes;

    FileCategory(SupportedFileExtension... types) {
        supportedTypes = Set.of(types);
    }
}

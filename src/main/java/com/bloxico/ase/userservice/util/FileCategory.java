package com.bloxico.ase.userservice.util;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.env.Environment;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.UUID;

import static com.bloxico.ase.userservice.web.error.ErrorCodes.AmazonS3.FILE_SIZE_EXCEEDED;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.AmazonS3.FILE_TYPE_NOT_SUPPORTED_FOR_CATEGORY;

public enum FileCategory {
    CV (SupportedFileExtension.pdf, SupportedFileExtension.doc, SupportedFileExtension.txt) {

        @Override
        public void validate(MultipartFile file, FileCategorySizeValidatorImpl validator) {
            if(!CV.supportedTypes.contains(SupportedFileExtension.getByExtension(FilenameUtils.getExtension(file.getOriginalFilename()))))
                throw FILE_TYPE_NOT_SUPPORTED_FOR_CATEGORY.newException();
            if(validator.validateCV(FileUtil.toKiloBytes(file.getSize())))
                throw FILE_SIZE_EXCEEDED.newException();
        }

        @Override
        public String generateFilePath(Environment environment) {
            return environment.getProperty("upload.cv.file-path");
        }
    },
    IMAGE (SupportedFileExtension.jpg, SupportedFileExtension.png) {

        @Override
        public void validate(MultipartFile file, FileCategorySizeValidatorImpl validator) {
            if(!IMAGE.supportedTypes.contains(SupportedFileExtension.getByExtension(FilenameUtils.getExtension(file.getOriginalFilename()))))
                throw FILE_TYPE_NOT_SUPPORTED_FOR_CATEGORY.newException();
            if(validator.validateImage(FileUtil.toKiloBytes(file.getSize())))
                throw FILE_SIZE_EXCEEDED.newException();
        }

        @Override
        public String generateFilePath(Environment environment) {
            return environment.getProperty("upload.image.file-path") + UUID.randomUUID().toString() + "/";
        }
    };

    public abstract void validate(MultipartFile file, FileCategorySizeValidatorImpl validator);

    public abstract String generateFilePath(Environment environment);

    private final Set<SupportedFileExtension> supportedTypes;

    FileCategory(SupportedFileExtension... types) {
        supportedTypes = Set.of(types);
    }
}

package com.bloxico.ase.userservice.util;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import static com.bloxico.ase.userservice.web.error.ErrorCodes.AmazonS3.*;

public enum FileCategory {
    CV {

        @Override
        public void validate(MultipartFile file, FileCategorySizeValidatorImpl validator) {
            if(!SupportedFileTypes.CV_SUPPORTED.contains(SupportedFileTypes.getByExtension(FilenameUtils.getExtension(file.getOriginalFilename()))))
                throw FILE_TYPE_NOT_SUPPORTED_FOR_CATEGORY.newException();
            if(validator.validateCV(FileUtil.toKiloBytes(file.getSize())))
                throw FILE_SIZE_EXCEEDED.newException();
        }
    },
    IMAGE {

        @Override
        public void validate(MultipartFile file, FileCategorySizeValidatorImpl validator) {
            if(!SupportedFileTypes.IMAGE_SUPPORTED.contains(SupportedFileTypes.getByExtension(FilenameUtils.getExtension(file.getOriginalFilename()))))
                throw FILE_TYPE_NOT_SUPPORTED_FOR_CATEGORY.newException();
            if(validator.validateImage(FileUtil.toKiloBytes(file.getSize())))
                throw FILE_SIZE_EXCEEDED.newException();
        }
    };

    public abstract void validate(MultipartFile file, FileCategorySizeValidatorImpl validator);
}

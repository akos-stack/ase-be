package com.bloxico.ase.userservice.util;

import org.springframework.core.env.Environment;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.UUID;
import java.util.function.UnaryOperator;

import static com.bloxico.ase.userservice.util.SupportedFileExtension.*;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.AmazonS3.FILE_SIZE_EXCEEDED;
import static com.bloxico.ase.userservice.web.error.ErrorCodes.AmazonS3.FILE_TYPE_NOT_SUPPORTED_FOR_CATEGORY;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.io.FilenameUtils.getExtension;

public enum FileCategory {

    CV(
            "upload.cv.file-path",
            "upload.file.max-cv-size",
            path -> path,
            pdf, doc, txt),

    IMAGE(
            "upload.image.file-path",
            "upload.file.max-image-size",
            path -> path + UUID.randomUUID().toString() + "/",
            jpg, png);

    private final String pathProperty, maxSizeProperty;
    private final UnaryOperator<String> generateFilePathFn;
    private final Set<SupportedFileExtension> supportedTypes;

    FileCategory(String pathProperty,
                 String maxSizeProperty,
                 UnaryOperator<String> generateFilePathFn,
                 SupportedFileExtension... types)
    {
        this.pathProperty = pathProperty;
        this.maxSizeProperty = maxSizeProperty;
        this.generateFilePathFn = generateFilePathFn;
        supportedTypes = Set.of(types);
    }

    public String generateFilePath(Environment environment) {
        return generateFilePathFn.apply(environment.getProperty(pathProperty));
    }

    public void validate(MultipartFile file, Environment environment) {
        if (!supportedTypes.contains(getByExtension(getExtension(file.getOriginalFilename()))))
            throw FILE_TYPE_NOT_SUPPORTED_FOR_CATEGORY.newException();
        long maxFileSizeKb = Long.parseLong(requireNonNull(environment.getProperty(maxSizeProperty)));
        if ((file.getSize() / 1024) > maxFileSizeKb)
            throw FILE_SIZE_EXCEEDED.newException();
    }

}

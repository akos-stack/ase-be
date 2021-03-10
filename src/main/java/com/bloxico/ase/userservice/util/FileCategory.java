package com.bloxico.ase.userservice.util;

import com.bloxico.ase.userservice.web.error.ErrorCodes;
import lombok.Getter;
import org.springframework.core.env.Environment;
import org.springframework.web.multipart.MultipartFile;

import java.util.EnumSet;
import java.util.Set;
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
            path -> path,
            jpg, png),

    CERTIFICATE(
            "upload.certificate.file-path",
            "upload.file.max-certificate-size",
            path -> path,
            pdf, doc, txt),

    PRINCIPAL_IMAGE(
            "upload.image.file-path",
            "upload.file.max-image-size",
            path -> path,
            jpg, png);

    private final String pathProperty, maxSizeProperty;
    private final UnaryOperator<String> generateFilePathFn;

    @Getter
    private final Set<SupportedFileExtension> supportedFileExtensions;

    FileCategory(String pathProperty,
                 String maxSizeProperty,
                 UnaryOperator<String> generateFilePathFn,
                 SupportedFileExtension... extensions)
    {
        this.pathProperty = pathProperty;
        this.maxSizeProperty = maxSizeProperty;
        this.generateFilePathFn = generateFilePathFn;
        supportedFileExtensions = Set.of(extensions);
    }

    public String genFilePath(Environment environment) {
        return generateFilePathFn.apply(environment.getProperty(pathProperty));
    }

    public void validate(MultipartFile file, Environment environment) {
        if (!supportedFileExtensions.contains(getByExtension(getExtension(file.getOriginalFilename()))))
            throw FILE_TYPE_NOT_SUPPORTED_FOR_CATEGORY.newException();
        long maxFileSizeKb = Long.parseLong(requireNonNull(environment.getProperty(maxSizeProperty)));
        if ((file.getSize() / 1024) > maxFileSizeKb)
            throw FILE_SIZE_EXCEEDED.newException();
    }

    public Set<ErrorCodes.AmazonS3> fileErrors(MultipartFile file, Environment environment) {
        var errors = EnumSet.noneOf(ErrorCodes.AmazonS3.class);
        if (!supportedFileExtensions.contains(getByExtension(getExtension(file.getOriginalFilename()))))
            errors.add(FILE_TYPE_NOT_SUPPORTED_FOR_CATEGORY);
        long maxFileSizeKb = Long.parseLong(requireNonNull(environment.getProperty(maxSizeProperty)));
        if ((file.getSize() / 1024) > maxFileSizeKb)
            errors.add(FILE_SIZE_EXCEEDED);
        return errors;
    }

}

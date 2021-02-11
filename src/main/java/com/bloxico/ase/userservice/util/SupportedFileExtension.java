package com.bloxico.ase.userservice.util;

import lombok.Getter;

import static com.bloxico.ase.userservice.web.error.ErrorCodes.AmazonS3.FILE_TYPE_NOT_SUPPORTED;

public enum SupportedFileExtension {

    jpg("image/jpg"),
    png("image/png"),
    txt("text/plain"),
    pdf("application/pdf"),
    doc("application/msword");

    @Getter
    private final String contentType;

    SupportedFileExtension(String contentType) {
        this.contentType = contentType;
    }

    public static SupportedFileExtension getByExtension(String extension) {
        try {
            return SupportedFileExtension.valueOf(extension);
        } catch (Throwable throwable) {
            throw FILE_TYPE_NOT_SUPPORTED.newException();
        }
    }

}

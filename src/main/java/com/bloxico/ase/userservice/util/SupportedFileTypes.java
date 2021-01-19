package com.bloxico.ase.userservice.util;

import lombok.Getter;

import static com.bloxico.ase.userservice.web.error.ErrorCodes.AmazonS3.FILE_TYPE_NOT_SUPPORTED;

@Getter
public enum SupportedFileTypes {

    JPG("jpg"),
    PNG("png"),
    TXT("txt"),
    PDF("pdf"),
    DOC("doc");

    private final String name;

    SupportedFileTypes(String name) {
        this.name = name;
    }

    public static void checkIsSupported(String extension) {
        try {
            SupportedFileTypes.valueOf(extension);
        } catch (Throwable __) {
            throw FILE_TYPE_NOT_SUPPORTED.newException();
        }
    }

}

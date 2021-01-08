package com.bloxico.ase.userservice.util;

import java.util.Arrays;

import static com.bloxico.ase.userservice.web.error.ErrorCodes.AmazonS3.FILE_TYPE_NOT_SUPPORTED;

public enum SupportedFileTypes {
    JPG("jpg"),
    PNG("png"),
    TXT("txt"),
    PDF("pdf"),
    DOC("doc");

    private String name;

    SupportedFileTypes(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static void checkIsSupported(String extension) {
        boolean supported = Arrays.stream(SupportedFileTypes.values()).anyMatch(fileType -> fileType.getName().equals(extension));
        if(!supported) {
            throw FILE_TYPE_NOT_SUPPORTED.newException();
        }
    }
}

package com.bloxico.ase.userservice.util;

import static com.bloxico.ase.userservice.web.error.ErrorCodes.AmazonS3.FILE_TYPE_NOT_SUPPORTED;

public enum SupportedFileExtension {

    jpg, png, txt, pdf, doc;

    public static SupportedFileExtension getByExtension(String extension) {
        try {
            return SupportedFileExtension.valueOf(extension);
        } catch (Throwable throwable) {
            throw FILE_TYPE_NOT_SUPPORTED.newException();
        }
    }

}

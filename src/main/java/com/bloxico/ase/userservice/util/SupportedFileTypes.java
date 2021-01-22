package com.bloxico.ase.userservice.util;

import java.util.EnumSet;

import static com.bloxico.ase.userservice.web.error.ErrorCodes.AmazonS3.FILE_TYPE_NOT_SUPPORTED;

public enum SupportedFileTypes {
    jpg, png, txt, pdf, doc;

    public static final EnumSet<SupportedFileTypes> CV_SUPPORTED = EnumSet.of(pdf, doc, txt);
    public static final EnumSet<SupportedFileTypes> IMAGE_SUPPORTED = EnumSet.of(jpg, png);

    public static SupportedFileTypes getByExtension(String extension) {
        SupportedFileTypes fileType;
        try {
            fileType = SupportedFileTypes.valueOf(extension);
        } catch (Throwable throwable) {
            throw FILE_TYPE_NOT_SUPPORTED.newException();
        }
        return fileType;
    }

}

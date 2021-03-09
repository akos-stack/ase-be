package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.util.FileCategory;
import com.bloxico.ase.userservice.web.model.aws.ValidateFilesRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.bloxico.ase.testutil.Util.genFileBytes;
import static com.bloxico.ase.testutil.Util.genUUID;
import static java.util.function.Predicate.not;

@Component
public class UtilS3 {

    public static FileCategory findOtherCategory(FileCategory category) {
        return Arrays.stream(FileCategory.values())
                .filter(not(cat -> cat
                        .getSupportedFileExtensions()
                        .stream()
                        .anyMatch(category.getSupportedFileExtensions()::contains)))
                .findAny()
                .orElseThrow();
    }

    public ValidateFilesRequest savedValidatedFilesRequest(FileCategory category) {
        MultipartFile imageFile = new MockMultipartFile("files",
                genUUID() + ".jpg", "image/jpg", genFileBytes(FileCategory.IMAGE));
        MultipartFile imageFile2 = new MockMultipartFile("fileItem",
                genUUID() + ".jpg", "image/jpg", genFileBytes(FileCategory.IMAGE));
        var files = new ArrayList(){{add(imageFile); add(imageFile2);}};
        return new ValidateFilesRequest(files, category);
    }

    public List<MultipartFile> savedListOfFiles(){
        MultipartFile imageFile = new MockMultipartFile("files",
                genUUID() + ".jpg", "image/jpg", genFileBytes(FileCategory.IMAGE));
        MultipartFile imageFile2 = new MockMultipartFile("fileItem",
                genUUID() + ".jpg", "image/jpg", genFileBytes(FileCategory.IMAGE));
        var files = new ArrayList(){{add(imageFile); add(imageFile2);}};
        return files;
    }
}

package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.util.FileCategory;
import com.bloxico.ase.userservice.web.model.aws.ValidateFilesRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.bloxico.ase.testutil.Util.genFileBytes;
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
        var imageBytes = genFileBytes(FileCategory.IMAGE);
        MultipartFile multipartFile = new MockMultipartFile("files",
                "testImg.jpg", "image/jpg", imageBytes);
        var imageBytes2 = genFileBytes(FileCategory.IMAGE);
        MultipartFile multipartFile2 = new MockMultipartFile("fileItem",
                "testImg2.jpg", "image/jpg", imageBytes2);
        var files = new ArrayList<MultipartFile>();
        files.add(multipartFile);
        files.add(multipartFile2);
        return new ValidateFilesRequest(files, category);
    }

    public List<MultipartFile> validateListOfFiles(){
        var imageBytes = genFileBytes(FileCategory.IMAGE);
        MultipartFile multipartFile = new MockMultipartFile("files",
                "testImg.jpg", "image/jpg", imageBytes);
        var imageBytes2 = genFileBytes(FileCategory.IMAGE);
        MultipartFile multipartFile2 = new MockMultipartFile("fileItem",
                "testImg2.jpg", "image/jpg", imageBytes2);
        var files = new ArrayList<MultipartFile>();
        files.add(multipartFile);
        files.add(multipartFile2);
        return files;
    }
}

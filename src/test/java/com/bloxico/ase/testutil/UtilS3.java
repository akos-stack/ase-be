package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.util.FileCategory;
import com.bloxico.ase.userservice.web.model.aws.ValidateFilesRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.bloxico.ase.testutil.Util.*;
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
        return new ValidateFilesRequest(new ArrayList(){{
            add(genMultipartFile(randElt(category.getSupportedFileExtensions()),
                genInvalidFileBytes(category)));
            {add(genMultipartFile(randElt(category.getSupportedFileExtensions()),
                    genInvalidFileBytes(category)));}}}, category);
    }

    public ValidateFilesRequest savedValidatedFilesRequest(FileCategory category, List<MultipartFile>files) {
        return new ValidateFilesRequest(files, category);
    }

    public List<MultipartFile> savedListOfFiles(FileCategory category){
        return new ArrayList(){{add(genMultipartFile(randElt(category.getSupportedFileExtensions()),
                genFileBytes(category)));
            {add(genMultipartFile(randElt(category.getSupportedFileExtensions()),
                    genFileBytes(category)));}}};
    }

    public List<MultipartFile> savedListOfInvalidFiles(FileCategory category){
        return new ArrayList(){{add(genMultipartFile(randElt(category.getSupportedFileExtensions()),
                genInvalidFileBytes(category)));
            {add(genMultipartFile(randElt(category.getSupportedFileExtensions()),
                    genInvalidFileBytes(category)));}}};
    }
}

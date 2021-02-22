package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.util.FileCategory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

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
}

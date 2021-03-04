package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.entity.BaseEntity;
import com.bloxico.ase.userservice.util.FileCategory;
import com.bloxico.ase.userservice.util.SupportedFileExtension;
import com.bloxico.ase.userservice.web.model.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import static java.util.concurrent.ThreadLocalRandom.current;
import static java.util.function.Predicate.not;
import static org.apache.commons.io.IOUtils.toByteArray;

public class Util {

    private Util() {
        throw new AssertionError();
    }

    public static final String ERROR_CODE = "errorCode";

    private static final String DEFAULT_LOCAL_PART
            = randElt(List.of("aseUserX", "aseUserY", "aseUserZ"));

    private static final AtomicLong LONG = new AtomicLong(0);

    public static String genPassword() {
        return LONG.incrementAndGet() + "_passWORD!";
    }

    public static String genEmail() {
        return genEmail(DEFAULT_LOCAL_PART);
    }

    public static String genEmail(String localPart) {
        return LONG.incrementAndGet() + "_" + localPart + "@mail.com";
    }

    public static int genPosInt(int bound) {
        return current().nextInt(1, bound);
    }

    public static BigDecimal genPosBigDecimal(double bound) {
        return BigDecimal.valueOf(current().nextDouble(1, bound));
    }

    public static String genUUID() {
        return UUID.randomUUID().toString();
    }

    public static LocalDateTime genExpiredLDT() {
        return LocalDateTime.now().minusHours(1);
    }

    public static LocalDateTime genNonExpiredLDT() {
        return LocalDateTime.now().plusHours(1);
    }

    public static MultipartFile genMultipartFile() {
        return genMultipartFile(randEnumConst(SupportedFileExtension.class));
    }

    public static MultipartFile genMultipartFile(FileCategory category) {
        return genMultipartFile(randElt(category.getSupportedFileExtensions()));
    }

    public static MultipartFile genMultipartFile(SupportedFileExtension extension) {
        var fileName = "file." + extension.toString();
        return new MockMultipartFile(
                fileName, fileName,
                extension.getContentType(),
                genUUID().getBytes());
    }

    public static String getTestFilePath(FileCategory category) {
        switch (category) {
            case CV:
                return "/testFiles/testCv.txt";
            case IMAGE:
                return "/testFiles/testImg.jpg";
            default:
                throw new IllegalArgumentException(category.toString());
        }
    }

    public static byte[] genFileBytes(FileCategory category) {
        try {
            return toByteArray(Util.class.getResourceAsStream(getTestFilePath(category)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T randElt(Collection<? extends T> coll) {
        return List.copyOf(coll).get(current().nextInt(0, coll.size()));
    }

    public static <T> T randElt(List<? extends T> list) {
        return list.get(current().nextInt(0, list.size()));
    }

    public static <T extends Enum<T>> T randEnumConst(Class<T> type) {
        return randElt(List.of(type.getEnumConstants()));
    }

    public static <T extends Enum<T>> T randOtherEnumConst(T eConst) {
        return Stream
                .generate(() -> randEnumConst(eConst.getDeclaringClass()))
                .filter(not(eConst::equals))
                .findAny()
                .orElseThrow();
    }

    public static void copyBaseEntityData(BaseEntity from, BaseEntity to) {
        to.setCreatorId(from.getCreatorId());
        to.setUpdaterId(from.getUpdaterId());
        to.setCreatedAt(from.getCreatedAt());
        to.setUpdatedAt(from.getUpdatedAt());
    }

    public static Map<String, Object> allPages(String k, Object v) {
        var queryParams = new HashMap<String, Object>();
        queryParams.put("page", 0);
        queryParams.put("size", Integer.MAX_VALUE);
        queryParams.put(k, v);
        return queryParams;
    }

    public static PageRequest allPages() {
        return new PageRequest(0, Integer.MAX_VALUE, null, null);
    }

}

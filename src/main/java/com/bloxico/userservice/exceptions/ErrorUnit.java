package com.bloxico.userservice.exceptions;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ErrorUnit {

    public ErrorUnit(String key, String value) {
        this.errorInformation = new HashMap<>();
        this.errorInformation.put(key, value);
    }

    public ErrorUnit(Map<String, String> errorInformation) {
        this.errorInformation = errorInformation;
    }

    private Map<String,String> errorInformation;

    public static class TransactionKeys {
        public static final String EMAIL = "email";
        public static final String LOW_ENRG_AMOUNT = "lowAmount";

        public static final String MISSING_IDS = "missingIds";
        public static final String NOT_CANCELABLE_IDS = "notCancelableIds";


    }
}

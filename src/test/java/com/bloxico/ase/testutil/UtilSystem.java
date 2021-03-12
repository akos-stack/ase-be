package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.util.EnumConstants;
import com.bloxico.ase.userservice.web.model.system.SystemConstantsResponse;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertTrue;

@Component
public class UtilSystem {

    public static void assertValidSystemConstants(SystemConstantsResponse response) {
        assertValidSystemConstants(response.getConstants());
    }

    public static void assertValidSystemConstants(Map<String, Object> systemConstants) {
        var enumConstants = Arrays
                .stream(EnumConstants.values())
                .map(EnumConstants::name)
                .collect(toSet());
        assertTrue(systemConstants.keySet().containsAll(enumConstants));
        assertTrue(systemConstants.containsKey("PERMISSION"));
    }

}

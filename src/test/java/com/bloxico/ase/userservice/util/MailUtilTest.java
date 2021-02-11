package com.bloxico.ase.userservice.util;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.userservice.util.MailUtil.Template;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.Util.genEmail;
import static com.bloxico.ase.testutil.Util.genUUID;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
public class MailUtilTest extends AbstractSpringTest {

    @Autowired
    private MailUtil mailUtil;

    @Test
    public void sendTokenEmail_nullArguments() {
        assertThrows(
                NullPointerException.class,
                () -> mailUtil.sendTokenEmail(null, genUUID(), genUUID()));
    }

    @Test
    public void sendTokenEmail_nullEmail() {
        for (var template : Template.values())
            assertThrows(
                    NullPointerException.class,
                    () -> mailUtil.sendTokenEmail(template, null, genUUID()));
    }

    @Test
    public void sendTokenEmail_nullToken() {
        for (var template : Template.values())
            assertThrows(
                    NullPointerException.class,
                    () -> mailUtil.sendTokenEmail(template, genUUID(), null));
    }

    @Test
    public void sendTokenEmail() {
        var email = genEmail();
        var token = genUUID();
        log.info("Generated email: " + email);
        for (var template : Template.values())
            mailUtil.sendTokenEmail(template, email, token);
    }

}

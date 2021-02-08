package com.bloxico.ase.userservice.util;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.userservice.util.MailUtil.Template;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.MockUtil.genEmail;
import static com.bloxico.ase.testutil.MockUtil.uuid;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
public class MailUtilTest extends AbstractSpringTest {

    @Autowired
    private MailUtil mailUtil;

    @Test
    public void sendTokenEmail_nullArguments() {
        assertThrows(
                NullPointerException.class,
                () -> mailUtil.sendTokenEmail(null, uuid(), uuid()));
    }

    @Test
    public void sendTokenEmail_nullEmail() {
        for (var template : Template.values())
            assertThrows(
                    NullPointerException.class,
                    () -> mailUtil.sendTokenEmail(template, null, uuid()));
    }

    @Test
    public void sendTokenEmail_nullToken() {
        for (var template : Template.values())
            assertThrows(
                    NullPointerException.class,
                    () -> mailUtil.sendTokenEmail(template, uuid(), null));
    }

    @Test
    public void sendTokenEmail() {
        var email = genEmail();
        var token = uuid();
        log.info("Generated email: " + email);
        for (var template : Template.values())
            mailUtil.sendTokenEmail(template, email, token);
    }

}

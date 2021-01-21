package com.bloxico.ase.userservice.util;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.userservice.util.MailUtil.Template;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.MockUtil.uuid;

@Slf4j
public class MailUtilTest extends AbstractSpringTest {

    @Autowired
    private MailUtil mailUtil;

    @Test(expected = NullPointerException.class)
    public void sendTokenEmail_nullTemplate() {
        mailUtil.sendTokenEmail(null, uuid(), uuid());
    }

    @Test(expected = NullPointerException.class)
    public void sendTokenEmail_nullEmail() {
        for (var template : Template.values())
            mailUtil.sendTokenEmail(template, null, uuid());
    }

    @Test(expected = NullPointerException.class)
    public void sendTokenEmail_nullToken() {
        for (var template : Template.values())
            mailUtil.sendTokenEmail(template, uuid(), null);
    }

    @Test
    public void sendTokenEmail() {
        var email = uuid() + "@mailinator.com";
        var token = uuid();
        log.info("Generated email: " + email);
        for (var template : Template.values())
            mailUtil.sendTokenEmail(template, email, token);
    }

}

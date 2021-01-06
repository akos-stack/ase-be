package com.bloxico.ase.userservice.util;

import com.bloxico.ase.testutil.AbstractSpringTest;
import com.bloxico.ase.userservice.util.MailUtil.Template;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bloxico.ase.testutil.MockUtil.randEnumConst;
import static com.bloxico.ase.testutil.MockUtil.uuid;
import static com.bloxico.ase.userservice.util.MailUtil.Template.*;

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
        mailUtil.sendTokenEmail(randEnumConst(Template.class), null, uuid());
    }

    @Test(expected = NullPointerException.class)
    public void sendTokenEmail_nullToken() {
        mailUtil.sendTokenEmail(randEnumConst(Template.class), uuid(), null);
    }

    @Test
    public void sendTokenEmail() {
        var email = uuid() + "@mailinator.com";
        var token = uuid();
        log.info("Generated email: " + email);
        mailUtil.sendTokenEmail(VERIFICATION, email, token);
        mailUtil.sendTokenEmail(RESET_PASSWORD, email, token);
        mailUtil.sendTokenEmail(EVALUATOR_INVITATION, email, token);
    }

}

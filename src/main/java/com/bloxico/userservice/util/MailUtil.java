package com.bloxico.userservice.util;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class MailUtil {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.support.email}")
    private String supportEmail;

    @Autowired
    @Qualifier("htmlPebbleEngine")
    private PebbleEngine pebbleEngine;

    @Value("classpath:images/enrglogo.jpg")
    private Resource logoImage;


    public void sendVerificationTokenEmail(String email, String verificationTokenValue) {
        log.debug("Sending Verification Token mail to email: {}", email);
        MimeMessagePreparator messagePreparator = createVerificationEmailAsPreparator(email, verificationTokenValue);
        sendMail(messagePreparator);
    }

    private MimeMessagePreparator createVerificationEmailAsPreparator(String email, String verificationTokenValue) {

        String tokenKey = "token";
        String enrgLogoKey = "logo";

        String subject = MailUtilConstants.SubjectConstants.VERIFICATION_EMAIL_SUBJECT;

        Map<String, Object> emailModel;
        emailModel = new HashMap<>();
        emailModel.put(tokenKey, verificationTokenValue);
        emailModel.put(enrgLogoKey, logoImage);

        MimeMessagePreparator preparator = prepareMimeMail(email, subject, emailModel, MailUtilConstants.TemplateConstants.VERIFICATION_EMAIL_TEMPLATE);

        return preparator;
    }

    public void sendResetPasswordTokenEmail(String email, String tokenValue) {
        log.debug("Sending Password Reset Token mail to email: {}", email);

        MimeMessagePreparator messagePreparator = createPasswordResetEmailAsPreparator(email, tokenValue);
        sendMail(messagePreparator);
    }

    private MimeMessagePreparator createPasswordResetEmailAsPreparator(String email, String tokenValue) {

        String tokenKey = "token";
        String enrgLogoKey = "logo";

        String subject = MailUtilConstants.SubjectConstants.RESET_PASSWORD_MAIL_SUBJECT;

        Map<String, Object> emailModel;
        emailModel = new HashMap<>();
        emailModel.put(tokenKey, tokenValue);
        emailModel.put(enrgLogoKey, logoImage);

        MimeMessagePreparator preparator = prepareMimeMail(email, subject, emailModel, MailUtilConstants.TemplateConstants.RESET_PASSWORD_MAIL_TEMPLATE);

        return preparator;
    }

    private MimeMessagePreparator prepareMimeMail(String recipientAddress, String subject, Map model, String pebbleTemplate) {

        String templateString = getTemplate(pebbleTemplate, model);

        MimeMessagePreparator preparator = new MimeMessagePreparator() {

            public void prepare(MimeMessage mimeMessage) throws MessagingException {

                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);
                message.setTo(recipientAddress);
                message.setSubject(subject);
                message.setText(templateString, true);
            }
        };
        return preparator;
    }

    private String getTemplate(String templateName, Map model) {
        PebbleTemplate template = pebbleEngine.getTemplate(templateName);
        Writer writer = new StringWriter();

        try {
            template.evaluate(writer, model);
        } catch (IOException e) {
            log.error("Count evaluate pebble template: {}", templateName);
            throw new RuntimeException("Pebble template failed to evaluate.");
        }

        return writer.toString();
    }

    private void sendMail(MimeMessagePreparator preparator) {
        log.debug("Mail prepared, sending...");
        mailSender.send(preparator);
    }

    private static class MailUtilConstants {
        private static class TemplateConstants {
            private static final String VERIFICATION_EMAIL_TEMPLATE = "verificationMailTemplate";
            private static final String RESET_PASSWORD_MAIL_TEMPLATE = "resetPasswordMailTemplate";
        }

        private static class SubjectConstants {
            private static final String VERIFICATION_EMAIL_SUBJECT = "EnergyCoin Dashboard - Registration Confirmation";
            private static final String RESET_PASSWORD_MAIL_SUBJECT = "EnergyCoin Dashboard - Forgotten password retrieval";

        }
    }
}
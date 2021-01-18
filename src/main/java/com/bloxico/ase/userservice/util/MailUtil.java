package com.bloxico.ase.userservice.util;

import com.mitchellbosecke.pebble.PebbleEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@Slf4j
@Component
public class MailUtil {

    public enum Template {

        VERIFICATION(
                "verificationMailTemplate",
                "Art Stock Exchange - Registration confirmation"),

        RESET_PASSWORD(
                "resetPasswordMailTemplate",
                "Art Stock Exchange - Forgotten password retrieval"),

        EVALUATOR_INVITATION(
                "evaluatorInvitationMailTemplate",
                "Art Stock Exchange - Evaluator invitation");

        private final String name, subject;

        Template(String name, String subject) {
            this.name = name;
            this.subject = subject;
        }

    }

    @Value("classpath:images/enrglogo.jpg")
    private Resource logoImage;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    @Qualifier("htmlPebbleEngine")
    private PebbleEngine pebbleEngine;

    public void sendTokenEmail(Template template, String email, String token) {
        log.debug("Sending {} mail to: {} with token: {}", template, email, token);
        requireNonNull(template);
        requireNonNull(email);
        requireNonNull(token);
        var text = getTemplate(template.name, Map.of("token", token, "logo", logoImage));
        sendMail(mimeMessage -> {
            var message = new MimeMessageHelper(mimeMessage, true);
            message.setTo(email);
            message.setSubject(template.subject);
            message.setText(text, true);
        });
    }

    private String getTemplate(String name, Map<String, Object> model) {
        var template = pebbleEngine.getTemplate(name);
        try (var writer = new StringWriter()) {
            template.evaluate(writer, model);
            return writer.toString();
        } catch (IOException ex) {
            log.error("Pebble template failed to evaluate:", ex);
            log.error("Failed for name: {}, model: {}", name, model);
            throw new RuntimeException(ex);
        }
    }

    private void sendMail(MimeMessagePreparator message) {
        log.debug("Mail prepared, sending...");
        mailSender.send(message);
        log.debug("Mail sent");
    }

}

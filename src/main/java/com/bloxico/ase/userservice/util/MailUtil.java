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
                "verify",
                "Art Stock Exchange - Registration confirmation"),

        RESET_PASSWORD(
                "resetPasswordMailTemplate",
                "reset",
                "Art Stock Exchange - Forgotten password retrieval"),

        EVALUATOR_INVITATION(
                "evaluatorInvitationMailTemplate",
                "register",
                "Art Stock Exchange - Evaluator invitation"),

        HOST_INVITATION(
                "hostInvitationMailTemplate",
                        "register",
                        "Art Stock Exchange - Host invitation");

        private final String name, relUri, subject;

        Template(String name, String relUri, String subject) {
            this.name = name;
            this.relUri = relUri;
            this.subject = subject;
        }

        public String uri(String contextPath) {
            return contextPath + "/" + relUri;
        }

    }

    @Value("${redirect.token.context-path}")
    private String contextPath;

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
        var model = Map.of(
                "path", template.uri(contextPath),
                "token", token,
                "logo", logoImage);
        var text = getTemplate(template.name, model);
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

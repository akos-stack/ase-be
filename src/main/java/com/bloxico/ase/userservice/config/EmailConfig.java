package com.bloxico.ase.userservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.*;

import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.Properties;

@Configuration
public class EmailConfig {

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private boolean auth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String startTlsEnable;

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.protocol}")
    private String protocol;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Bean
    @Profile("!test")
    public JavaMailSender notTestMailSender() {
        var sender = new JavaMailSenderImpl();

        var properties = new Properties();
        properties.put("mail.smtp.auth", auth);
        properties.put("mail.smtp.starttls.enable", startTlsEnable);

        sender.setJavaMailProperties(properties);
        sender.setHost(host);
        sender.setPort(port);
        sender.setProtocol(protocol);
        sender.setUsername(username);
        sender.setPassword(password);

        return sender;
    }

    /**
     * Empty mail sender is used to prevent sending email in test environment.
     **/
    @Bean
    @Profile("test")
    public JavaMailSender testMailSender() {
        return new JavaMailSender() {
            @Override
            public MimeMessage createMimeMessage() {
                return null;
            }

            @Override
            public MimeMessage createMimeMessage(InputStream contentStream) throws MailException {
                return null;
            }

            @Override
            public void send(MimeMessage mimeMessage) throws MailException {
            }

            @Override
            public void send(MimeMessage... mimeMessages) throws MailException {
            }

            @Override
            public void send(MimeMessagePreparator mimeMessagePreparator) throws MailException {
            }

            @Override
            public void send(MimeMessagePreparator... mimeMessagePreparators) throws MailException {
            }

            @Override
            public void send(SimpleMailMessage simpleMessage) throws MailException {
            }

            @Override
            public void send(SimpleMailMessage... simpleMessages) throws MailException {
            }
        };
    }

}

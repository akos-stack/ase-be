package com.bloxico.userservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.Properties;

@Configuration
public class EmailConfig {


    @Value("${spring.mail.host}")
    private String host;
    @Value("${spring.mail.port}")
    private int port;
    @Value("${spring.mail.properties.mail.smtp.auth}")
    private boolean auth;
    @Value("${spring.mail.protocol}")
    private String protocol;
    @Value("${spring.mail.username}")
    private String username;
    @Value("${spring.mail.password}")
    private String password;
    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String startTlsEnable;

    @Bean
    @Profile("!test")
    public JavaMailSender notTestMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        Properties mailProperties = new Properties();
        mailProperties.put("mail.smtp.auth", auth);
        mailProperties.put("mail.smtp.starttls.enable", startTlsEnable);

        javaMailSender.setJavaMailProperties(mailProperties);
        javaMailSender.setHost(host);
        javaMailSender.setPort(port);
        javaMailSender.setProtocol(protocol);
        javaMailSender.setUsername(username);
        javaMailSender.setPassword(password);

        return javaMailSender;
    }

    @Bean
    @Profile("test")
    //empty mail sender used to prevent sending email in test environment
    public JavaMailSender testMailSender() {
        JavaMailSender javaMailSender = new JavaMailSender() {
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
        return javaMailSender;
    }


}

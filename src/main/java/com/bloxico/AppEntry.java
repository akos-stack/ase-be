package com.bloxico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class AppEntry {

    public static void main(final String[] args) {
        SpringApplication.run(AppEntry.class, args);
    }

}

package com.bloxico.ase.userservice.config;

import com.bloxico.ase.userservice.filter.*;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.spring.PebbleViewResolver;
import com.mitchellbosecke.pebble.spring.extension.SpringExtension;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.util.unit.DataSize;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.DispatcherType;
import javax.servlet.MultipartConfigElement;
import java.util.EnumSet;

import static org.springframework.util.unit.DataUnit.MEGABYTES;

@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final String SENTRY_DSN = "sentry.dsn";

    private final Environment env;

    @Autowired
    public WebConfig(Environment env) {
        this.env = env;
    }

    @PostConstruct
    public void init() {
        String sentryUrl = env.getRequiredProperty(SENTRY_DSN);
        log.info("Setting Sentry URL: '{}'", sentryUrl);
        System.setProperty(SENTRY_DSN, sentryUrl);
    }

    @Primary
    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        var filter = new CommonsRequestLoggingFilter();
        filter.setIncludeClientInfo(true);
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        return filter;
    }

    @Bean
    public Loader<String> loader() {
        return new ClasspathLoader();
    }

    @Bean(name = "htmlPebbleEngine")
    public PebbleEngine pebbleEngine() {
        return new PebbleEngine.Builder()
                .loader(loader())
                .extension(springExtension())
                .build();
    }

    @Bean
    public ViewResolver viewResolver() {
        var viewResolver = new PebbleViewResolver();
        viewResolver.setPrefix("templates/");
        viewResolver.setSuffix(".html");
        viewResolver.setPebbleEngine(pebbleEngine());
        return viewResolver;
    }

    @Bean
    public SpringExtension springExtension() {
        return new SpringExtension();
    }

    @Bean
    public RepeatableReadRequestFilter requestCachingFilter() {
        return new RepeatableReadRequestFilter();
    }

    @Bean
    public FilterRegistrationBean<MdcFilter> filterRegistration() {
        var registration = new FilterRegistrationBean<>(new MdcFilter());
        registration.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
        return registration;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // TODO make it more strict
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600); // sec
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        var factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.of(20, MEGABYTES));
        factory.setMaxRequestSize(DataSize.of(20, MEGABYTES));
        return factory.createMultipartConfig();
    }

}
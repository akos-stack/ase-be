package com.bloxico.ase.userservice.config;

import com.bloxico.ase.userservice.filter.CommonsRequestLoggingFilter;
import com.bloxico.ase.userservice.filter.MdcFilter;
import com.bloxico.ase.userservice.filter.RepeatableReadRequestFilter;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.spring.PebbleViewResolver;
import com.mitchellbosecke.pebble.spring.extension.SpringExtension;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import javax.servlet.DispatcherType;
import java.util.EnumSet;

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

}
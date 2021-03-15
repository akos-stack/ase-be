package com.bloxico.ase.userservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.servlet.MultipartConfigElement;
import java.util.Date;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

@Configuration
public class CommonBeansConfig {

    @Bean
    public ObjectMapper createJacksonMapper() {
        return new ObjectMapper()
                .configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .registerTypeAdapter(
                        Date.class,
                        (JsonDeserializer<Date>) (jsonElement, type, context)
                                -> new Date(jsonElement.getAsJsonPrimitive().getAsLong()))
                .create();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateErrorHandler restTemplateErrorHandler) {
        return new RestTemplateBuilder()
                .errorHandler(restTemplateErrorHandler)
                .build();
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("1024MB");
        factory.setMaxRequestSize("1024MB");
        return factory.createMultipartConfig();
    }

}

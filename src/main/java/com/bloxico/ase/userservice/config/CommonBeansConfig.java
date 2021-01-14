package com.bloxico.ase.userservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

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

}
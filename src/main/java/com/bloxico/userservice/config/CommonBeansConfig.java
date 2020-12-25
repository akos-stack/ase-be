package com.bloxico.userservice.config;

import com.bloxico.userservice.util.resttemplate.DefaultRestTemplateResponseHandler;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.Date;

@Configuration

public class CommonBeansConfig {

    @Bean
    public ObjectMapper createJacksonMapper() {
        ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return objectMapper;
    }

    /**
     * Bean used to encode/decode passwords
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Autowired
//    CoinUserDetailsService coinUserDetailsService;
//
//    @Bean
//    public DaoAuthenticationProvider daoAuthenticationProvider(PasswordEncoder encoder) {
//        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
//        daoAuthenticationProvider.setPasswordEncoder(encoder);
//        daoAuthenticationProvider.setUserDetailsService(coinUserDetailsService);
//
//        return daoAuthenticationProvider;
//    }

    @Bean
    public Gson gson() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
                        return new Date(jsonElement.getAsJsonPrimitive().getAsLong());
                    }
                })
                .create();

        return gson;
    }

    @Bean
    public RestTemplate restTemplate(DefaultRestTemplateResponseHandler defaultErrorHandler) {
        return new RestTemplateBuilder().errorHandler(defaultErrorHandler).build();
    }
}

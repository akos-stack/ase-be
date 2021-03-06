package com.bloxico.userservice.config.oauth2;

import com.bloxico.userservice.filter.RepeatableReadRequestFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

@Configuration
@EnableResourceServer
public class ResourceServerOAuth2Config extends ResourceServerConfigurerAdapter {


    private RepeatableReadRequestFilter repeatableReadRequestFilter;

    public ResourceServerOAuth2Config(RepeatableReadRequestFilter repeatableReadRequestFilter) {
        this.repeatableReadRequestFilter = repeatableReadRequestFilter;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(this.repeatableReadRequestFilter, AbstractPreAuthenticatedProcessingFilter.class)
                .authorizeRequests().anyRequest().authenticated()
                .and()
                .csrf().disable();
    }
}

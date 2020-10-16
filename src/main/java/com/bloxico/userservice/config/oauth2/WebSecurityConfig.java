package com.bloxico.userservice.config.oauth2;

import com.bloxico.userservice.web.api.UserPasswordApi;
import com.bloxico.userservice.web.api.UserRegistrationApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@Configuration("WebSecurityConfig")
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DaoAuthenticationProvider daoAuthenticationProvider;

    @Value("${front.end.url}")
    protected String FRONTEND_URL;


    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    private static final String[] AUTH_WHITELIST;

    static {
        AUTH_WHITELIST = new String[]{
                "/swagger-resources/**", "/swagger-ui.html", "/api-docs", "/webjars/**",
                UserRegistrationApi.REGISTRATION_ENDPOINT,
                UserRegistrationApi.REGISTRATION_CONFIRMATION_ENDPOINT,
                UserRegistrationApi.REGISTRATION_TOKEN_REFRESH_ENDPOINT,
                UserRegistrationApi.REGISTRATION_TOKEN_RESEND_ENDPOINT,
                UserRegistrationApi.REGISTRATION_PAGE_DATA_ENDPOINT,
                UserPasswordApi.FORGOT_PASSWORD_ENDPOINT,
                UserPasswordApi.UPDATE_FORGOTTEN_PASSWORD_ENDPOINT,
                UserPasswordApi.FORGOT_PASSWORD_TOKEN_RESEND_ENDPOINT,

        };
    }

    @Profile(value = "prod")
    @Bean
    public FilterRegistrationBean prodCorsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin(FRONTEND_URL);
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    @Profile(value = "!prod")
    @Bean
    public FilterRegistrationBean notProdCorsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring().antMatchers(AUTH_WHITELIST);
    }

    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(daoAuthenticationProvider);
    }
}
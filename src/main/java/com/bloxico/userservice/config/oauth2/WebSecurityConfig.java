package com.bloxico.userservice.config.oauth2;

import com.bloxico.ase.userservice.config.OAuth2FailureHandler;
import com.bloxico.ase.userservice.config.OAuth2SuccessHandler;
import com.bloxico.ase.userservice.filter.JwtAuthorizationFilter;
import com.bloxico.ase.userservice.service.token.ITokenBlacklistService;
import com.bloxico.ase.userservice.service.user.impl.UserProfileServiceImpl;
import com.bloxico.userservice.filter.RepeatableReadRequestFilter;
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
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@EnableWebSecurity
@Configuration("WebSecurityConfig")
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

//    @Autowired
//    private DaoAuthenticationProvider daoAuthenticationProvider;

    @Autowired
    TokenStore tokenStore;

    @Autowired
    ITokenBlacklistService tokenBlacklistService;

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

                com.bloxico.ase.userservice.web.api.UserRegistrationApi.REGISTRATION_ENDPOINT,
                com.bloxico.ase.userservice.web.api.UserRegistrationApi.REGISTRATION_CONFIRM_ENDPOINT,
                com.bloxico.ase.userservice.web.api.UserRegistrationApi.REGISTRATION_TOKEN_REFRESH_ENDPOINT,
                com.bloxico.ase.userservice.web.api.UserRegistrationApi.REGISTRATION_TOKEN_RESEND_ENDPOINT,

                com.bloxico.ase.userservice.web.api.UserPasswordApi.PASSWORD_FORGOT_ENDPOINT,
                com.bloxico.ase.userservice.web.api.UserPasswordApi.PASSWORD_UPDATE_FORGOTTEN_ENDPOINT,
                com.bloxico.ase.userservice.web.api.UserPasswordApi.PASSWORD_TOKEN_RESEND_ENDPOINT

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
        web.ignoring().antMatchers(AUTH_WHITELIST);
    }

    private final DefaultOAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;

    @Autowired
    public WebSecurityConfig(UserProfileServiceImpl oAuth2UserService,
                             OAuth2SuccessHandler oAuth2SuccessHandler,
                             OAuth2FailureHandler oAuth2FailureHandler)
    {
        this.oAuth2UserService = oAuth2UserService;
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
        this.oAuth2FailureHandler = oAuth2FailureHandler;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/oauth2/**")
                .permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(STATELESS)
                .and()
                .oauth2Login()
                .authorizationEndpoint()
                .baseUri("/oauth2/authorize")
                //.authorizationRequestRepository(cookieAuthorizationRequestRepository())
                .and()
                .redirectionEndpoint()
                .baseUri("/oauth2/callback/*")
                .and()
                .userInfoEndpoint()
                .userService(oAuth2UserService)
                .and()
                .successHandler(oAuth2SuccessHandler)
                .failureHandler(oAuth2FailureHandler)
                .and()
                .addFilterBefore(
                        new JwtAuthorizationFilter(tokenBlacklistService, tokenStore),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(
                        new RepeatableReadRequestFilter(),
                        AbstractPreAuthenticatedProcessingFilter.class)
                .csrf().disable();
    }

}
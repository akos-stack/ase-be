package com.bloxico.ase.userservice.config.security;

import com.bloxico.ase.userservice.filter.JwtAuthorizationFilter;
import com.bloxico.ase.userservice.filter.RepeatableReadRequestFilter;
import com.bloxico.ase.userservice.service.token.ITokenBlacklistService;
import com.bloxico.ase.userservice.web.api.UserPasswordApi;
import com.bloxico.ase.userservice.web.api.UserRegistrationApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.endpoint.*;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static org.springframework.security.config.BeanIds.AUTHENTICATION_MANAGER;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${front.end.url}")
    protected String FRONTEND_URL;

    private final TokenStore tokenStore;
    private final ITokenBlacklistService tokenBlacklistService;
    private final DefaultOAuth2UserService oAuth2UserService;
    private final OAuthSuccessHandler oAuthSuccessHandler;
    private final OAuthFailureHandler oAuthFailureHandler;
    private final CookieOAuthRequestRepository cookieOAuthRequestRepository;

    @Autowired
    public WebSecurityConfig(TokenStore tokenStore,
                             ITokenBlacklistService tokenBlacklistService,
                             AseSecurityService oAuth2UserService,
                             OAuthSuccessHandler oAuthSuccessHandler,
                             OAuthFailureHandler oAuthFailureHandler,
                             CookieOAuthRequestRepository cookieOAuthRequestRepository)
    {
        this.tokenStore = tokenStore;
        this.tokenBlacklistService = tokenBlacklistService;
        this.oAuth2UserService = oAuth2UserService;
        this.oAuthSuccessHandler = oAuthSuccessHandler;
        this.oAuthFailureHandler = oAuthFailureHandler;
        this.cookieOAuthRequestRepository = cookieOAuthRequestRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(name = AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    @Profile(value = "prod")
    public FilterRegistrationBean<CorsFilter> prodCorsFilter() {
        var source = new UrlBasedCorsConfigurationSource();
        var config = new CorsConfiguration();
        config.addAllowedOrigin(FRONTEND_URL);
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        var bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(HIGHEST_PRECEDENCE);
        return bean;
    }

    @Bean
    @Profile(value = "!prod")
    public FilterRegistrationBean<CorsFilter> notProdCorsFilter() {
        var source = new UrlBasedCorsConfigurationSource();
        var config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        var bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(HIGHEST_PRECEDENCE);
        return bean;
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(
                "/oauth2/authorize",

                "/swagger-resources/**",
                "/swagger-ui.html",
                "/api-docs",
                "/webjars/**",

                UserRegistrationApi.REGISTRATION_ENDPOINT,
                UserRegistrationApi.REGISTRATION_CONFIRM_ENDPOINT,
                UserRegistrationApi.REGISTRATION_TOKEN_REFRESH_ENDPOINT,
                UserRegistrationApi.REGISTRATION_TOKEN_RESEND_ENDPOINT,
                UserRegistrationApi.REGISTRATION_EVALUATOR_SUBMIT,
                UserRegistrationApi.REGISTRATION_ART_OWNER_SUBMIT,
                UserRegistrationApi.REGISTRATION_EVALUATOR_INVITATION_CHECK,

                UserPasswordApi.PASSWORD_FORGOT_ENDPOINT,
                UserPasswordApi.PASSWORD_UPDATE_FORGOTTEN_ENDPOINT,
                UserPasswordApi.PASSWORD_TOKEN_RESEND_ENDPOINT
        );
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()

                .sessionManagement()
                .sessionCreationPolicy(STATELESS)
                .and()

                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()

                .authorizeRequests()

                .anyRequest().authenticated()
                .and()

                .oauth2Login()

                .authorizationEndpoint()
                .baseUri("/oauth2/authorize")
                .authorizationRequestRepository(cookieOAuthRequestRepository)
                .and()

                .redirectionEndpoint()
                .baseUri("/oauth2/callback/*")
                .and()

                .userInfoEndpoint()
                .userService(oAuth2UserService)
                .and()

                .tokenEndpoint()
                .accessTokenResponseClient(authCodeTokenResponseClient())

                .and()
                .successHandler(oAuthSuccessHandler)
                .failureHandler(oAuthFailureHandler)

                .and()

                .addFilterBefore(
                        new JwtAuthorizationFilter(tokenBlacklistService, tokenStore),
                        UsernamePasswordAuthenticationFilter.class)

                .addFilterBefore(
                        new RepeatableReadRequestFilter(),
                        AbstractPreAuthenticatedProcessingFilter.class);
    }

    private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> authCodeTokenResponseClient() {
        var tokenResponseHttpMessageConverter = new OAuth2AccessTokenResponseHttpMessageConverter();
        tokenResponseHttpMessageConverter.setTokenResponseConverter(new OAuth2AccessTokenResponseConverterWithDefaults());

        var restTemplate = new RestTemplate(List.of(new FormHttpMessageConverter(), tokenResponseHttpMessageConverter));
        restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());

        var tokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
        tokenResponseClient.setRestOperations(restTemplate);

        return tokenResponseClient;
    }

}

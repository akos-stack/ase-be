package com.bloxico.userservice.config.oauth2;

import com.bloxico.ase.userservice.config.security.*;
import com.bloxico.ase.userservice.filter.JwtAuthorizationFilter;
import com.bloxico.ase.userservice.service.token.ITokenBlacklistService;
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
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${front.end.url}")
    protected String FRONTEND_URL;

    private final TokenStore tokenStore;
    private final ITokenBlacklistService tokenBlacklistService;
    private final DefaultOAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final CookieOAuth2RequestRepository cookieOAuth2RequestRepository;

    @Autowired
    public WebSecurityConfig(TokenStore tokenStore,
                             ITokenBlacklistService tokenBlacklistService,
                             AseSecurityService oAuth2UserService,
                             OAuth2SuccessHandler oAuth2SuccessHandler,
                             OAuth2FailureHandler oAuth2FailureHandler,
                             CookieOAuth2RequestRepository cookieOAuth2RequestRepository)
    {
        this.tokenStore = tokenStore;
        this.tokenBlacklistService = tokenBlacklistService;
        this.oAuth2UserService = oAuth2UserService;
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
        this.oAuth2FailureHandler = oAuth2FailureHandler;
        this.cookieOAuth2RequestRepository = cookieOAuth2RequestRepository;
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    @Profile(value = "prod")
    public FilterRegistrationBean<CorsFilter> prodCorsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin(FRONTEND_URL);
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        var bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    @Bean
    @Profile(value = "!prod")
    public FilterRegistrationBean<CorsFilter> notProdCorsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        var bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    private static final String[] AUTH_WHITELIST;

    static {
        AUTH_WHITELIST = new String[]{

                "/oauth2/authorize",
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

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(AUTH_WHITELIST);
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
                .authorizationRequestRepository(cookieOAuth2RequestRepository)
                .and()

                .redirectionEndpoint()
                .baseUri("/oauth2/callback/*")
                .and()

                .userInfoEndpoint()
                .userService(oAuth2UserService)
                .and()

                .tokenEndpoint()
                .accessTokenResponseClient(authorizationCodeTokenResponseClient())

                .and()
                .successHandler(oAuth2SuccessHandler)
                .failureHandler(oAuth2FailureHandler)

                .and()

                .addFilterBefore(
                        new JwtAuthorizationFilter(tokenBlacklistService, tokenStore),
                        UsernamePasswordAuthenticationFilter.class)

                .addFilterBefore(
                        new RepeatableReadRequestFilter(),
                        AbstractPreAuthenticatedProcessingFilter.class);
    }

    private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> authorizationCodeTokenResponseClient() {
        OAuth2AccessTokenResponseHttpMessageConverter tokenResponseHttpMessageConverter =
                new OAuth2AccessTokenResponseHttpMessageConverter();
        tokenResponseHttpMessageConverter.setTokenResponseConverter(new OAuth2AccessTokenResponseConverterWithDefaults());

        RestTemplate restTemplate = new RestTemplate(Arrays.asList(
                new FormHttpMessageConverter(), tokenResponseHttpMessageConverter));
        restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());

        DefaultAuthorizationCodeTokenResponseClient tokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
        tokenResponseClient.setRestOperations(restTemplate);

        return tokenResponseClient;

    }
}
package com.bloxico.ase.userservice.config;

import org.springframework.context.annotation.*;
import springfox.documentation.builders.*;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

@Profile("!prod")
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket sampleServiceApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .securityContexts(List.of(securityContext()))
                .securitySchemes(List.of(securitySchema()))
                .apiInfo(apiInfo());
    }

    private static SecurityContext securityContext() {
        return SecurityContext
                .builder()
                .securityReferences(List.of(securityReference()))
                .build();
    }

    private static SecurityReference securityReference() {
        var scopes = new AuthorizationScope[]{
                new AuthorizationScope("read", "read all"),
                new AuthorizationScope("trust", "trust all"),
                new AuthorizationScope("write", "write all")};
        return new SecurityReference("oauth2", scopes);
    }

    private static OAuth securitySchema() {
        var scopes = List.of(
                new AuthorizationScope("read", "read all"),
                new AuthorizationScope("write", "access all"));
        var grantTypes = List.<GrantType>of(
                new ResourceOwnerPasswordCredentialsGrant(""));
        return new OAuth("oauth2", scopes, grantTypes);
    }

    private static ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("ASE Dashboard API")
                .description("This is the Art Stock Exchange API definition.")
                .contact(new Contact("Art Stock Exchange API", "", ""))
                .license("")
                .licenseUrl("")
                .version("1.0.0")
                .build();
    }

}
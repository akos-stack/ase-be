package com.bloxico.userservice.config.oauth2;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.token.TokenStore;

import javax.sql.DataSource;

@Configuration
@Slf4j
public class TokenStoreConfig {

    @Autowired
    private Environment env;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        String driverClassName = env.getProperty("spring.datasource.driver-class-name");
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(env.getProperty("spring.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.password"));
        dataSource.setSchema(env.getProperty("schema.name"));

        return dataSource;
    }

    @Bean
    public TokenStore tokenStore() {
        return new CustomJdbcTokenStore(dataSource());
    }

    @Bean
    public ApprovalStore approvalStore() {
        return new JdbcApprovalStore(dataSource());
    }
}

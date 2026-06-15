package edu.univ.erp.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    @Qualifier("authDataSource")
    public DataSource authDataSource(
            @Value("${app.datasource.auth.url}") String url,
            @Value("${app.datasource.auth.username}") String username,
            @Value("${app.datasource.auth.password}") String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setPoolName("auth-pool");
        config.setMaximumPoolSize(10);
        return new HikariDataSource(config);
    }

    @Bean
    @Qualifier("erpDataSource")
    public DataSource erpDataSource(
            @Value("${app.datasource.erp.url}") String url,
            @Value("${app.datasource.erp.username}") String username,
            @Value("${app.datasource.erp.password}") String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setPoolName("erp-pool");
        config.setMaximumPoolSize(10);
        return new HikariDataSource(config);
    }
}

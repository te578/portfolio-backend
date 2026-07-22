package com.example.demo.config;

import com.example.demo.tenant.TenantRoutingDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String baseUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${app.tenant.schemas}")
    private String[] schemaNames;

    @Bean
    @Primary
    public DataSource dataSource() {
        Map<Object, Object> schemaDataSources = new HashMap<>();

        for (String schemaName : schemaNames) {
            schemaDataSources.put(schemaName, buildDataSourceForSchema(schemaName));
        }

        TenantRoutingDataSource routingDataSource = new TenantRoutingDataSource();
        routingDataSource.setTargetDataSources(schemaDataSources);
        // schemaCdがマップに無い場合のフォールバック接続先(通常は使われない想定)
        routingDataSource.setDefaultTargetDataSource(schemaDataSources.values().iterator().next());
        routingDataSource.afterPropertiesSet();

        return routingDataSource;
    }

    private DataSource buildDataSourceForSchema(String schemaName) {
        String separator = baseUrl.contains("?") ? "&" : "?";
        String urlWithSchema = baseUrl + separator + "currentSchema=" + schemaName;

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(urlWithSchema);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }
}

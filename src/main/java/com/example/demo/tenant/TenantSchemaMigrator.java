package com.example.demo.tenant;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// 起動時に、設定されたスキーマ(demo, demo2など)ひとつひとつに対して
// 同じマイグレーションファイル(V1〜)を流す。スキーマごとに独立したflyway_schema_historyが作られる
@Component
public class TenantSchemaMigrator implements CommandLineRunner {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${app.tenant.schemas}")
    private String[] schemaNames;

    @Override
    public void run(String... args) {
        for (String schemaName : schemaNames) {
            Flyway.configure()
                    .dataSource(url, username, password)
                    .schemas(schemaName)
                    .locations("classpath:db/migration")
                    .load()
                    .migrate();
        }
    }
}

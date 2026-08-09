package com.example.demo.tenant;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

// リクエストごとに保持しているschemaCdを見て、接続先のDataSource(=接続先スキーマ)を切り替える
public class TenantRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return TenantContext.getSchemaCd();
    }
}

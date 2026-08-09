package com.example.demo.tenant;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

// URLの先頭セグメントをスキーマコードとして取り出し、Controllerにはそのセグメントを取り除いたパスを渡すFilter
// 例: /demo/api/auth/login へのリクエスト
//     -> schemaCd = "demo"
//     -> Controllerには /api/auth/login として渡す(Controller側のマッピングは変更不要)
// 先頭セグメントがapp.tenant.schemasに登録されていない場合は、
// テナントコードが付いていないリクエスト(例: localhostからの直接アクセス)とみなし、
// デフォルトスキーマを使ってパスはそのままControllerに渡す
@Component
public class TenantFilter implements Filter {

    @Value("${app.tenant.schemas}")
    private String[] schemaNames;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String uri = httpRequest.getRequestURI();

        // 先頭の"/"を除いてから"/"で区切り、最初の要素をスキーマコード候補とする
        String[] segments = uri.replaceFirst("^/", "").split("/", 2);
        String firstSegment = segments[0];

        String schemaCd;
        String remainingPath;

        if (isKnownSchema(firstSegment)) {
            // 先頭セグメントが登録済みスキーマ名だったので、テナントコードとして取り除く
            schemaCd = firstSegment;
            remainingPath = segments.length > 1 ? "/" + segments[1] : "/";
        } else {
            // 登録済みスキーマ名ではなかったので、テナントコード無しのリクエストとみなす
            schemaCd = schemaNames[0];
            remainingPath = uri;
        }

        TenantContext.setSchemaCd(schemaCd);

        try {
            chain.doFilter(new TenantPathRequestWrapper(httpRequest, remainingPath), response);
        } finally {
            // スレッドプールで同じスレッドが次のリクエストに使い回されるため、必ずクリアする
            TenantContext.clear();
        }
    }

    // 先頭セグメントがapp.tenant.schemasに登録されているスキーマ名かどうかを判定する
    private boolean isKnownSchema(String segment) {
        for (String schemaName : schemaNames) {
            if (schemaName.equals(segment)) {
                return true;
            }
        }
        return false;
    }

    // getRequestURI()とgetServletPath()だけをスキーマコードを除いたパスに差し替えるラッパー
    private static class TenantPathRequestWrapper extends HttpServletRequestWrapper {

        private final String remainingPath;

        TenantPathRequestWrapper(HttpServletRequest request, String remainingPath) {
            super(request);
            this.remainingPath = remainingPath;
        }

        @Override
        public String getRequestURI() {
            return remainingPath;
        }

        @Override
        public String getServletPath() {
            return remainingPath;
        }
    }
}

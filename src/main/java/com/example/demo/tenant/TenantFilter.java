package com.example.demo.tenant;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.stereotype.Component;

import java.io.IOException;

// URLの先頭セグメントをスキーマコードとして取り出し、Controllerにはそのセグメントを取り除いたパスを渡すFilter
// 例: /demo/api/auth/login へのリクエスト
//     -> schemaCd = "demo"
//     -> Controllerには /api/auth/login として渡す(Controller側のマッピングは変更不要)
@Component
public class TenantFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String uri = httpRequest.getRequestURI();

        // 先頭の"/"を除いてから"/"で区切り、最初の要素をスキーマコードとする
        String[] segments = uri.replaceFirst("^/", "").split("/", 2);
        String schemaCd = segments[0];
        String remainingPath = segments.length > 1 ? "/" + segments[1] : "/";

        TenantContext.setSchemaCd(schemaCd);

        try {
            chain.doFilter(new TenantPathRequestWrapper(httpRequest, remainingPath), response);
        } finally {
            // スレッドプールで同じスレッドが次のリクエストに使い回されるため、必ずクリアする
            TenantContext.clear();
        }
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

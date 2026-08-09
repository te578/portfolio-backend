package com.example.demo.tenant;

// リクエストごとのスキーマコード(demo, demo2など)をスレッド単位で保持するクラス
// staticなただの変数にすると別リクエスト間でテナントが混ざってしまうため、必ずThreadLocalを使う
public class TenantContext {

    private static final ThreadLocal<String> CURRENT_SCHEMA_CD = new ThreadLocal<>();

    public static void setSchemaCd(String schemaCd) {
        CURRENT_SCHEMA_CD.set(schemaCd);
    }

    public static String getSchemaCd() {
        return CURRENT_SCHEMA_CD.get();
    }

    // リクエスト終了時に必ず呼び出す(呼び忘れるとスレッドプールで次のリクエストに値が残ってしまう)
    public static void clear() {
        CURRENT_SCHEMA_CD.remove();
    }
}

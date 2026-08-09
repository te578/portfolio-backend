# マルチテナント対応(スキーマ分離)まとめ

## 何がしたいか

- 会社をいくつかのグループ(`demo`, `demo2`)に分ける
- グループごとにPostgreSQLの**スキーマを物理的に分ける**
- 1つのスキーマの中には**複数の会社**が入り、会社は`sites`テーブルの`site_id`/`company_cd`で区別する
- URLの先頭(`/demo/...` / `/demo2/...`)でどのグループ(スキーマ)向けかを判定する

## URLからDBスキーマが決まるまでの流れ

1. ブラウザ/クライアントが `http://localhost:8080/demo/api/auth/login` を叩く
2. **TenantFilter** がURLの先頭 `demo` を読み取り、`TenantContext`(リクエスト単位の入れ物)に保存する
3. Controller には `demo` を取り除いた `/api/auth/login` だけが渡る(Controller側は変更不要)
4. Repository がDBに問い合わせるとき、**TenantRoutingDataSource** が`TenantContext`の中身(`demo`)を見て、接続先を`demo`スキーマに自動で切り替える
5. 実際のSQL(`SELECT ... FROM users`など)は`demo`スキーマの`users`テーブルに対して実行される

`/demo2/...`で来たときも同じ流れで、今度は`demo2`スキーマに切り替わる。

## 作った/変更したファイル

| ファイル | 役割 |
|---|---|
| `tenant/TenantFilter.java` | URLの先頭セグメントを読み取り、Controllerに渡す前にパスを書き換える |
| `tenant/TenantContext.java` | 今のリクエストのスキーマコードを保持する(ThreadLocal) |
| `tenant/TenantRoutingDataSource.java` | `TenantContext`の中身を見て接続先スキーマを決める |
| `config/DataSourceConfig.java` | `demo`/`demo2`それぞれのDataSourceを作り、上のRoutingDataSourceに登録する |
| `tenant/TenantSchemaMigrator.java` | 起動時に、Flywayのマイグレーション(`V1〜V4`)を`demo`・`demo2`それぞれに適用する |
| `application.properties` | `app.tenant.schemas=demo,demo2` を追加、標準のFlyway自動実行(`public`のみ対象)を無効化 |
| `db/migration/V4__create_sites_table.sql` | `sites`テーブル(会社マスタ)を追加。`site_id`, `company_cd`, `company_nm`, `status`など |

## 動作確認の手順(再現方法)

```bash
# アプリ起動(devプロファイル)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# スキーマができているか確認
docker exec demo_db psql -U postgres -d demo -c "\dn"

# 各スキーマのテーブル確認
docker exec demo_db psql -U postgres -d demo -c "\dt demo.*"
docker exec demo_db psql -U postgres -d demo -c "\dt demo2.*"

# ユーザー登録(それぞれのスキーマに入ることを確認)
curl -X POST http://localhost:8080/demo/api/auth/register  -H "Content-Type: application/json" -d '{"name":"demo-user","email":"demo-user@example.com","password":"password123"}'
curl -X POST http://localhost:8080/demo2/api/auth/register -H "Content-Type: application/json" -d '{"name":"demo2-user","email":"demo2-user@example.com","password":"password123"}'

# データが混ざっていないか確認
docker exec demo_db psql -U postgres -d demo -c "SELECT id, name, email FROM demo.users;"
docker exec demo_db psql -U postgres -d demo -c "SELECT id, name, email FROM demo2.users;"
```

## まだやっていないこと(次回以降)

- 同じスキーマの中で「どの会社(`site_id`)のデータか」を絞り込む処理(`users`テーブルへの`site_id`外部キー追加など)
- 存在しないスキーマコード(`demo`/`demo2`以外)がURLに来たときのエラーハンドリング(今は500エラーになる)

## 気をつけたこと

- `docker exec demo_db psql ...` で直接確認する場合、DBeaverなど既存の接続はスキーマ一覧をキャッシュしていることがあるので、新しく作ったスキーマが見えないときはツリーを **Refresh** する
- `./mvnw spring-boot:run` をバックグラウンドで動かしたあと停止する場合、停止操作だけではJavaプロセスがポート8080に残ることがあるので注意(`netstat -ano | findstr :8080` で確認)

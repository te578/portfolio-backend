# Portfolio Backend API

Spring Boot 製のユーザー管理 API です。JWT 認証、PostgreSQL のスキーマ単位でのマルチテナント構成、Flyway によるマイグレーション管理を備えたバックエンドをポートフォリオとして実装しています。

[![Java](https://img.shields.io/badge/Java-25-orange?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.14-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?logo=postgresql)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/license-MIT-lightgrey)](#license)

## 目次

- [概要](#概要)
- [主な機能](#主な機能)
- [技術スタック](#技術スタック)
- [アーキテクチャ](#アーキテクチャ)
- [ディレクトリ構成](#ディレクトリ構成)
- [セットアップ](#セットアップ)
- [API エンドポイント](#api-エンドポイント)
- [テスト](#テスト)
- [ドキュメント](#ドキュメント)
- [License](#license)

## 概要

会員登録・ログインなどのユーザー管理機能を提供する REST API です。単一の DB インスタンス上で、URL のパス(`/demo/...` / `/demo2/...`)によって参照先の PostgreSQL スキーマを切り替えるマルチテナント構成を採用しており、テナントごとにデータを物理的に分離しています。

## 主な機能

- ユーザー登録 / ログイン(メールアドレス + パスワード)
- パスワードの BCrypt ハッシュ化
- JWT によるアクセストークン / リフレッシュトークンの発行
- URL パスに基づくマルチテナント DB ルーティング(スキーマ分離)
- Flyway によるスキーマごとのマイグレーション自動適用
- テナントコード未指定時のデフォルトスキーマへのフォールバック
- 共通例外ハンドラによるエラーレスポンスの統一
- OpenAPI(Swagger UI)によるドキュメント自動生成

## 技術スタック

| 分類 | 技術 |
|---|---|
| 言語 | Java 25 |
| フレームワーク | Spring Boot 3.5.14 |
| DB アクセス | MyBatis |
| マイグレーション | Flyway |
| 認証 | JWT (jjwt) / Spring Security Crypto (BCrypt) |
| データベース | PostgreSQL 16 |
| API ドキュメント | springdoc-openapi (Swagger UI) |
| テスト | JUnit 5 / Testcontainers |
| コンテナ | Docker / Docker Compose |

## アーキテクチャ

### レイヤー構成

```
Controller → Service → Repository → Database
```

責務ごとにパッケージを分割したレイヤードアーキテクチャです。

```
com.example.demo
├── controller/   … HTTP リクエストの受け口
├── service/      … 認証・登録などのビジネスロジック
├── repository/   … MyBatis を使った DB アクセス
├── dto/          … リクエスト/レスポンスの詰め替え用オブジェクト
├── entity/       … DB テーブルに対応するドメインオブジェクト
├── security/     … JWT の生成・検証
├── tenant/        … マルチテナント(スキーマ切り替え)関連
├── config/       … DataSource / CORS などの設定
└── exception/    … 共通例外ハンドラ
```

### マルチテナント(スキーマ分離)

リクエストの URL パス先頭(`/demo/...` / `/demo2/...`)を `TenantFilter` が読み取り、`TenantContext` に保存します。Repository が DB へアクセスする際、`TenantRoutingDataSource` がそのテナントコードを見て接続先スキーマを自動で切り替える仕組みです。

```
Client
  └─ POST /demo/api/auth/login
        │
        ▼
  TenantFilter … URL先頭 "demo" を読み取り TenantContext に保存
        │
        ▼
  Controller (/api/auth/login) … テナントを意識しない通常の処理
        │
        ▼
  Repository (MyBatis)
        │
        ▼
  TenantRoutingDataSource … TenantContext を見て接続先スキーマを切り替え
        │
        ▼
  PostgreSQL: demo スキーマ / demo2 スキーマ
```

詳細な設計・検証手順は [docs/multi-tenant.md](docs/multi-tenant.md) を参照してください。

## ディレクトリ構成

```
.
├── src/main/java/com/example/demo
│   ├── controller/   HealthController, UserController
│   ├── service/      UserService, TokenService
│   ├── repository/   UserRepository, TokenRepository, SiteRepository
│   ├── dto/           RequestDTO, ResponseDTO, TokenPair
│   ├── entity/        User, Profile, Site
│   ├── security/      JwtUtil
│   ├── tenant/         TenantFilter, TenantContext, TenantRoutingDataSource, TenantSchemaMigrator
│   ├── config/        DataSourceConfig, CorsConfig
│   └── exception/     GlobalExceptionHandler, 各種カスタム例外
├── src/main/resources
│   ├── application.properties
│   ├── application-dev.properties (git 管理外 / 各自作成)
│   └── db/migration                 Flyway マイグレーションファイル (V1〜V6)
├── src/test                          単体テスト / 結合テスト
├── docs/multi-tenant.md              マルチテナント設計メモ
├── Dockerfile
└── docker-compose.yml                ローカル用 PostgreSQL コンテナ
```

## セットアップ

### 必要環境

- Java 25
- Docker / Docker Compose(ローカル DB 用)

### 1. リポジトリの取得

```bash
git clone git@github.com:te578/portfolio-backend.git
cd portfolio-backend
```

### 2. データベースの起動

```bash
docker compose up -d
```

### 3. 環境変数の設定

`src/main/resources/application-dev.properties` に接続情報などの環境変数を設定してください(機密情報のためリポジトリには含まれていません)。

```properties
DB_HOST=localhost
DB_NAME=demo
DB_USER=postgres
DB_PASSWORD=your_password
```

### 4. アプリケーションの起動

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

起動後、`http://localhost:8080/ping` にアクセスして `ok` が返ってくれば起動成功です。

### 5. API ドキュメント(Swagger UI)

```
http://localhost:8080/swagger-ui.html
```

## API エンドポイント

| メソッド | パス | 説明 |
|---|---|---|
| GET | `/` , `/ping` | ヘルスチェック |
| POST | `/{tenant}/api/auth/register` | ユーザー登録 |
| POST | `/{tenant}/api/auth/login` | ログイン(アクセストークン / リフレッシュトークンを発行) |

`{tenant}` には `demo` または `demo2` を指定します(省略時はデフォルトスキーマにフォールバック)。

**ログインリクエスト例**

```bash
curl -X POST http://localhost:8080/demo/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"demo-user@example.com","password":"password123"}'
```

**レスポンス例**

```json
{
  "accessToken": "xxxxx.yyyyy.zzzzz",
  "refreshToken": "xxxxx.yyyyy.zzzzz",
  "role": 0
}
```

## テスト

```bash
./mvnw test
```

- `UserServiceImplTest` : 認証・登録ロジックの単体テスト
- `UserControllerTest` : コントローラ層のテスト
- `UserAuthenticationIT` : Testcontainers を使った結合テスト(実 DB を起動して検証)

## ドキュメント

- [docs/multi-tenant.md](docs/multi-tenant.md) — マルチテナント(スキーマ分離)の設計と動作確認手順

## License

このリポジトリはポートフォリオ公開用です。

# プロジェクト概要

Spring Boot　ユーザー管理API

## 絶対にやってはいけないこと

- .envファイルは読まない・編集しない
- APIキーやパスワードをコードに直書きしない
- 機密情報はapplication-dev.propertiesを読み込まない。編集しない
- 勝手にコードを追記修正しないでください

## 技術スタッ

- Java 25

## パッケージ構造

com.example.demo
├── controller/
├── service/
├── dto/
└── repository/

## 立ち上げのコマンドはこれを使います

./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
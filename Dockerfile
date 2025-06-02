# Java23の公式イメージ
FROM eclipse-temurin:23-jdk-alpine

WORKDIR /app

#jarファイルをコピー(先にビルドが必要となる)
COPY target/springprofilewebapp-0.3.0-PreRelease.jar app.jar

# ポート開放
EXPOSE 9137

# アプリ起動
ENTRYPOINT [ "java", "-jar", "app.jar" ]
# ProfileApp(SpringProfileApp)の概要
## なにこれ？
JavaSpringBootを使用したした簡単なプロフィールサイトです。
## 構築に必要なもの
* Java(推奨バージョン：Java23以降)
    * 動かすだけならランタイム(JRE)で大丈夫です。
    ただし、ソースコードからコンパイルする場合や、改造したい場合は、JDKが必要になります。
* MariaDB(推奨)
    * データベースにデータを保管するため、MariaDB等のデータベースも必要です。コードを書き換えることで、MySQLにも対応します
* Javaを動かすために必要なPC(WindowsでもMacでもLinuxでも)
* うごかしたいと思う気持ち
## 構築方法
### 手動構築する場合
* Java及びMariaDBをインストールする
* MariaDBのデータベースを構築する
    * MariaDbのデータベース作成権限のあるユーザで以下のSQLを実行します
    テーブルの作成は不要です。JPAが自動作成します
```sql
-- データベースを新規作成する
create database `作成したいデータベースの名前`
-- データベースの名前は任意の名前
```
* 環境変数を構築する
    * 詳しくは下記初期設定について項の環境変数の設定をご覧ください
* Javaでこのプログラムを起動する
```cmd
java -jar [xxxx.jarの名前]
```
こちらで起動完了です。

### Dockerで構築する場合
後日加筆
## 初期設定について
起動前に以下の環境変数の設定が必要です
| 環境変数名 | 変数の値 | 説明 |
| ---- | ---- | ---- |
| SPRING_DATASOURCE_USERNAME | DBのユーザー名 | DBはMariaDBを使うこと |
| SPRING_DATASOURCE_PASSWORD | DBのパスワード | DBはMariaDBを使うこと |
| SPRING_MAIL_HOST | 送信メールサーバ名 | |
| SPRING_MAIL_PORT | 送信ポート | |
| SPRING_MAIL_USERNAME | メールのユーザー名 | 通常はメールアドレス |
| SPRING_MAIL_PASSWORD | メールのパスワード | |
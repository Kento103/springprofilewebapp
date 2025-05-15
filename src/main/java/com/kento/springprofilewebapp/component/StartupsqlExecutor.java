package com.kento.springprofilewebapp.component;

import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StartupsqlExecutor implements CommandLineRunner{
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception{ // 起動時に実行したいコマンドは必ずrunとすること！他のメゾット名禁止。(CommandLineRunnerインターフェースのメゾットなので)
        System.out.println("[起動時情報ログ:info]データベースの存在確認コマンド開始");
        try {
            Integer result = jdbcTemplate.queryForObject(
                "SELECT 1 FROM categorys WHERE id = ? LIMIT 1", Integer.class, 1);

                if (result != null) {
                    System.out.println("[起動時情報ログ:info] categorysレコードはすでに存在します");
                }
        } catch (EmptyResultDataAccessException e) {
            System.out.println("[起動時情報ログ:execute]categoryテーブルに未分類作成");
            try {
                jdbcTemplate.update("INSERT INTO categorys (id, name) VALUES (?, ?)", 1, "未分類");
                System.out.println("作成成功");
            } catch (Exception ex) {
                System.out.println("失敗しました。");
            }
        }
        System.out.println("[起動時情報ログ:info]データベースの存在確認コマンド開始");
        try {
            Integer result = jdbcTemplate.queryForObject(
                "SELECT 1 FROM users WHERE id = ? LIMIT 1", Integer.class, 1);

                if (result != null) {
                    System.out.println("[起動時情報ログ:info] usersレコードはすでに存在します");
                }
        } catch (EmptyResultDataAccessException e) {
            System.out.println("[起動時情報ログ:execute]cusersテーブルに管理者作成");
            try {
                jdbcTemplate.update("INSERT INTO users (id, email, password, username, hurigana, age, sexial, role, locked, deleted) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 1, "admin@admin.com", "$2a$10$FKTWmnMLWWLDn8grWj1cEO5Niw9kspwkK9VO382OIwU.cQd5rH2ly", "管理者", "かんりしゃ", 0, 0, "ROLE_ADMIN", 0, 0);
                System.out.println("作成成功");
            } catch (Exception ex) {
                System.out.println("失敗しました。");
            }
        }
    }
}

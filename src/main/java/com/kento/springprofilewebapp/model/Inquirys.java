package com.kento.springprofilewebapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO
// データベースのテーブルに対応するエンティティを作成する
@Entity // エンティティ
@Table(name = "inquirys") // テーブル名
@NoArgsConstructor // デフォルトで引数なしのコンストラクタを生成
@AllArgsConstructor // すべてのフィールドを引数に受け取るコンストラクタを自動生成できる
@Data // クラスに付与する事で、全フィールドでゲッターセッターが使えるようになる
public class Inquirys {
    @Id // 主キー！
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自動採番！
    private int id;

    // 投稿したユーザー
    private int userId;

    // 選択されたカテゴリー
    private int category;

    // 本文
    private String description;

    // ステータス
    private int status;
}

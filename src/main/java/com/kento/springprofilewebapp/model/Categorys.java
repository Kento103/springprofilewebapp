package com.kento.springprofilewebapp.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO (Data Transfer Object)
// データベースのテーブルに対応するエンティティを作成。
@Entity // エンティティ
@Table(name = "categorys")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Categorys {
    @Id // 主キーに設定する！
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自動採番(Auto_incrementにする)
    private int id;

    // 内部結合用変数(inquirysと結合するための物)
    @OneToMany(mappedBy = "categorys", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inquirys> inquirysList;

    // カテゴリーの名前
    @NotBlank(message = "カテゴリー名を入力してください！")
    private String name;

    // カテゴリーの作成時間を記録する
    private LocalDateTime createAt;

    // カテゴリの編集時間を記録する
    private LocalDateTime updateAt;

    public Categorys(String name) {
        this.name = name; // カテゴリ名
        this.createAt = LocalDateTime.now(); // 現在時刻でアップデートする
    }
}

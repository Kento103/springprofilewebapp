package com.kento.springprofilewebapp.model;

import java.util.List;

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
    @OneToMany(mappedBy = "categorys")
    private List<Inquirys> inquirysList;

    // カテゴリーの名前
    @NotBlank(message = "カテゴリー名を入力してください！")
    private String name;

    public Categorys(String name) {
        this.name = name; // カテゴリ名
    }
}

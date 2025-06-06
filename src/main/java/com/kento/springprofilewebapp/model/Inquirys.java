package com.kento.springprofilewebapp.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
    @ManyToOne
    @JoinColumn(name = "user_id") // 自分のテーブル(この場合は、inquirysのどのテーブルとくっつけるかを名前にかく)
    @OnDelete(action = OnDeleteAction.CASCADE) // Hibernate拡張、userが削除された時に該当のものすべて削除する
    private Users users;

    // private int userId;

    // 選択されたカテゴリー
    @ManyToOne
    @JoinColumn(name = "category")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Categorys categorys;

    // private int category;

    // 本文
    @NotBlank(message = "本文を入力してください！")
    private String description;

    // メールアドレス
    @NotBlank(message = "{user.email.required}")
    @Email(message = "{user.email.invalided}")
    private String inquiryEmail;

    // ステータス
    private int status;

    // 作成時間
    private LocalDateTime createAt;

    // 更新時間
    private LocalDateTime updateAt;

    public Inquirys(String description, int categoryId, Users users, String inquiryEmail) {
        Categorys category = new Categorys();
        category.setId(categoryId); // 受け取ったカテゴリIDを挿入する
        //Users user = new Users();
        //user.setId(3); // いったん仮で1を挿入する
        this.description = description; // お問い合わせ内容
        this.categorys = category;
        this.users = users; // 投稿したユーザを代入する。
        this.status = 0; // 仮挿入
        this.inquiryEmail = inquiryEmail; // 返信希望Eメールアドレス
        this.createAt = LocalDateTime.now(); // 現在時刻を代入する
    }
}

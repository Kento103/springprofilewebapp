package com.kento.springprofilewebapp.model;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO (Data Transfer Object)
// データベースのテーブルに対応するエンティティを作成する。
@Entity
@Table(name = "users") // テーブル名を設定するためのアノテーション
@NoArgsConstructor // デフォルトで引数なしのコンストラクタを自動生成する
@AllArgsConstructor // すべてのフィールドを引数に受け取るコンストラクタを自動生成できる
@Data // クラスに付与することで、全フィールドでゲッターセッターが使えるようになる。
public class Users implements UserDetails {
    @Id // IDの主キーに設定する
    @GeneratedValue(strategy = GenerationType.IDENTITY) // startgy = GenerationType.IDENTITY自動採番する
    private int id;

    // validationメッセージ message={0}で全部の情報が出てくる
    @Size(min = 1, max = 255, message = "ユーザー名は255文字以内で入力してください") // バリデーション(1～255文字のみ許可する)
    @NotBlank(message = "このフィールドは必須です")
    @Column(nullable = false) // nulladle ..(nullを許可するか？)
    private String username;

    @Size(min = 1, max = 255, message = "メールアドレスは1～255文字以内で入力してください")
    @NotBlank(message = "このフィールドは必須です")
    @Email(message = "メールアドレスの正しい形式で入力してください")
    @Column(nullable = false, unique = true) //unique...一意でないと登録できないようにする
    private String email;

    //@Pattern(regexp = "^[a-zA-Z0-9_-]$", message = "パスワードは半角英数と数字、_-のみ使用出来ます")
    @NotBlank(message = "このフィールドは必須です")
    @Column(nullable = false)
    private String password; // パスワード(ハッシュ化する！)

    private String role; // 権限設定は必ず(ROLE_[権限とすること] 例）ROLE_USER、ROLE_ADMIN)

    @Size(min = 1, max = 255, message = "ふりがなは1～255文字以内で入力してください。")
    @NotBlank(message = "このフィールドは必須です")
    @Pattern(regexp = "^[ぁ-んー]*$", message = "ひらがなのみ入力できます")
    private String hurigana; // ふりがな(必須とはしない)

    public Users(String username, String email, String password, String role, String hurigana) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.hurigana = hurigana;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { //必ずこの変数にすること！
        return List.of(new SimpleGrantedAuthority(role));
    }

    // アカウントの削除確認
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // アカウントがロックされていないかBAN等の処理に用いる(falseでロック)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(nullable = false) // nulladle ..(nullを許可するか？)
    private String username;

    @Column(nullable = false, unique = true) //unique...一意でないと登録できないようにする
    private String email;

    @Column(nullable = false)
    private String password; // パスワード(ハッシュ化する！)

    private String role; // 権限設定は必ず(ROLE_[権限とすること] 例）ROLE_USER、ROLE_ADMIN)

    public Users(String username, String email, String password, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
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

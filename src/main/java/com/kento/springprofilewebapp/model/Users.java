package com.kento.springprofilewebapp.model;

import java.util.Collection;
import java.util.List;

import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO (Data Transfer Object)
// データベースのテーブルに対応するエンティティを作成する。
@Entity // エンティティだよ
@Table(name = "users") // テーブル名を設定するためのアノテーション
@NoArgsConstructor // デフォルトで引数なしのコンストラクタを自動生成する
@AllArgsConstructor // すべてのフィールドを引数に受け取るコンストラクタを自動生成できる
@Data // クラスに付与することで、全フィールドでゲッターセッターが使えるようになる。
@Where(clause = "deleted = false")  // デフォルトでdeleted = falseのデータのみを取得する
public class Users implements UserDetails {
    @Id // IDの主キーに設定する
    @GeneratedValue(strategy = GenerationType.IDENTITY) // startgy = GenerationType.IDENTITY自動採番する
    private int id;

    @OneToMany(mappedBy = "users") // 一体多の関係mappedBY="テーブル名"を指定する。
    private List<Inquirys> inquirysList; // 内部結合する為に使うもの(内部結合は必ずListとすること。)

    // Likesモデル用
    @OneToMany(mappedBy = "fromLikeUserId") // 一体多の関係(参照先の変数を指定すること！)
    private List<Likes> likesSent; // 内部結合するために使うもの

    // Likesモデル用
    @OneToMany(mappedBy = "toLikeUserId")
    private List<Likes> likesReception;

    // validationメッセージ message={0}で全部の情報が出てくる
    @Size(min = 1, max = 255, message = "ユーザー名は255文字以内で入力してください") // バリデーション(1～255文字のみ許可する) @Sizeはstring型のみ使える
    @NotBlank(message = "このフィールドは必須です") // @NotBlankはstring型のみ使える
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

    @Max(value = 3, message = "不正な値です")
    @NotNull(message = "性別を入力してください")
    @Column(nullable = false)
    private int sexial; // 性別設定用(0:設定なし,1:男性,2:女性,3:その他)

    @Size(min = 0, max = 1500, message = "自己紹介は最大1,500文字以内で入力してください")
    private String description; // 自己紹介保存用

    @Max(value = 999, message = "年齢は最大3桁までです")
    @NotNull(message = "年齢を入力してください")
    private int age; // 年齢(最大999歳まで許可)

    // ユーザーが削除フラグを立てて登録されているか確認する(論理削除):trueで削除フラグ
    private boolean deleted;

    // ユーザが無効(Ban)にされているか確認する:trueで無効化
    private boolean locked;

    // プロフィール画像のパスを保管するための変数
    private String imagePath; // 画像のURLパス

    public Users(String username, String email, String password, String role, String hurigana, String description, int sexial, int age) {
        this.username = username; // ユーザー名
        this.email = email; // メールアドレス
        this.password = password; // パスワード
        this.role = role; // 権限
        this.hurigana = hurigana; // ふりがな
        this.description = description; // 自己紹介
        this.sexial = sexial; // 性別
        this.age = age; // 年齢
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
        return !locked; // lockedの変数を読みに行く。false(lockd変数がtrue)の場合は、ログインさせない。
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

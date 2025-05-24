package com.kento.springprofilewebapp.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
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

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true) // 一体多の関係mappedBY="テーブル名"を指定する。
    private List<Inquirys> inquirysList; // 内部結合する為に使うもの(内部結合は必ずListとすること。)

    // Likesモデル用
    @OneToMany(mappedBy = "fromLikeUserId", cascade = CascadeType.ALL, orphanRemoval = true) // 一体多の関係(参照先の変数を指定すること！)
    private List<Likes> likesSent; // 内部結合するために使うもの

    // Likesモデル用
    @OneToMany(mappedBy = "toLikeUserId", cascade = CascadeType.ALL, orphanRemoval = true) // cascade = CascadeType.ALL, orphanRemoval = trueでこちらのクエリを削除すると該当するInquiryレコードもDBレベルで削除される
    private List<Likes> likesReception;

    // validationメッセージ message={0}で全部の情報が出てくる
    @Size(min = 1, max = 255, message = "ユーザー名は255文字以内で入力してください") // バリデーション(1～255文字のみ許可する) @Sizeはstring型のみ使える
    @NotBlank(message = "{user.username.required}") // @NotBlankはstring型のみ使える
    @Column(nullable = false) // nulladle ..(nullを許可するか？)
    private String username;

    @Size(min = 1, max = 255, message = "メールアドレスは1～255文字以内で入力してください")
    @NotBlank(message = "{user.email.required}")
    @Email(message = "{user.email.invalided}")
    @Column(nullable = false, unique = true) //unique...一意でないと登録できないようにする
    private String email;

    //@Pattern(regexp = "^[a-zA-Z0-9_-]$", message = "パスワードは半角英数と数字、_-のみ使用出来ます")
    @NotBlank(message = "{user.password.required}")
    @Column(nullable = false)
    private String password; // パスワード(ハッシュ化する！)

    @NotBlank(message = "{other.field.required}")
    private String role; // 権限設定は必ず(ROLE_[権限とすること] 例）ROLE_USER、ROLE_ADMIN)

    @Size(min = 1, max = 255, message = "{user.hurigana.wrong}")
    @NotBlank(message = "{user.hurigana.required}")
    @Pattern(regexp = "^[ぁ-んー]*$", message = "{user.hurigana.nothiragana}")
    private String hurigana; // ふりがな(必須とはしない)

    @Max(value = 3, message = "{user.sexial.wrong}")
    @NotNull(message = "{user.sexial.required}")
    @Column(nullable = false)
    private int sexial; // 性別設定用(0:設定なし,1:男性,2:女性,3:その他)

    @Size(min = 0, max = 1500, message = "{user.description.wrong}")
    @Lob // LargeObject...DBのデータ型がTEXT型となり改行も含め保存が可能となる。
    private String description; // 自己紹介保存用

    @Max(value = 999, message = "{user.age.invalided}")
    @NotNull(message = "{user.age.required}")
    private Integer age; // 年齢(最大999歳まで許可) ※プリミティブ型(int)では空の値はエラーとなる。そのためInteger(class)にしてnullを許容できるようにする

    // ユーザーが削除フラグを立てて登録されているか確認する(論理削除):trueで削除フラグ
    private boolean deleted;

    // ユーザが無効(Ban)にされているか確認する:trueで無効化
    private boolean locked;

    // プロフィール画像のパスを保管するための変数
    private String imagePath; // 画像のURLパス

    private LocalDateTime createAt; // ユーザの作成日

    private LocalDateTime updateAt; // ユーザの最終更新日

    private LocalDateTime deletedAt; // ユーザの削除日

    public Users(String username, String email, String password, String role, String hurigana, String description, int sexial, Integer age, boolean locked) {
        this.username = username; // ユーザー名
        this.email = email; // メールアドレス
        this.password = password; // パスワード
        this.role = role; // 権限
        this.hurigana = hurigana; // ふりがな
        this.description = description; // 自己紹介
        this.sexial = sexial; // 性別
        this.age = age; // 年齢
        this.locked = locked; // アクセス制御
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

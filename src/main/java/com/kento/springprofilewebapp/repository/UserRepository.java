package com.kento.springprofilewebapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.kento.springprofilewebapp.model.Users;

public interface UserRepository extends JpaRepository<Users, Integer>{
    Optional<Users> findByEmail(String email); // カスタムメゾット、メールアドレスで検索

    Page<Users> findAll(Pageable pageable); // ユーザー一覧の件数を制限して取得する

    // ユーザーに対して削除フラグを付与する(削除するときに呼び出す物)
    /**
     * ユーザーに削除フラグを立て、論理削除します。このメゾットを実行しても、データベースからは削除されません。
     * @param id 論理削除したいユーザーID
     */
    @Transactional
    default void softDelete(int id) { // idで対象ユーザを選択する
        findById(id).ifPresent(user -> {
            user.setDeleted(true); // 削除フラグを更新する
            save(user); // 情報をDBに保存する
        });
    }

    // ユーザに対して削除フラグを取り消す(復元するときに呼び出す物)
    // NotUsed(@Whereを使っているので使えない。)
    @Transactional
    default void softRecovery(int id) {
        findById(id).ifPresent(user -> { // idで対象ユーザーを選択する
            user.setDeleted(false); // 削除フラグを取り消し更新する
            save(user); // 情報をDBに保存する
        });
    }

    /*
     * 本来は上記の削除フラグを付与する方式のfalseで削除フラグを取り消し(false)にしたい。
     * しかし、モデルにて@Whereを入力してしまってるため、削除フラグがついているものは、検索出来ない。
     * そのため、直接クエリ文を記載し、フラグを書き換える。
     * メゾットはvoid型でも問題ないが、int型にすると更新件数が戻ってくる
     * // 例) int recoveryUser(@Param("id") int id); // 更新件数が返ってくる
     */
    /**
     * 論理削除したユーザーの削除フラグを無効にし、復元するためのメゾットです。
     * @param id 復元対象のユーザーID
     */
    @Transactional // @Modifyingを使うメゾットはデフォルトでトランザクションが適用されないため、@Transactionalアノテーションをつけてデータ変更を確実に反映させる
    @Modifying // UPDATEやDELETEなどのデータ変更クエリを実行する際、@Modifyingを追加する必要がある
    @Query(value = "UPDATE users SET users.deleted = FALSE WHERE users.id = :id", nativeQuery = true)
    void recoveryUser(@Param("id") int id); // @Paramを明示的につけて、クエリパラメータを確実にわたるようにする

    /**
     * ユーザー情報を削除(完全削除)する為のメゾットです。このメゾットを実装するとユーザのレコードは完全に消去されます。
     * @param id 削除対象のidを入力
     */
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM users WHERE users.id = :id", nativeQuery = true)
    void removeUser(@Param("id") int id);

    /*
     * 現状モデル(エンティティ)で削除フラグを除外している為、削除フラグが立っているユーザは表示されない。
     * そのため、削除フラグがついているユーザも含んで検索する場合は、SQL文を用いて、カスタムで実装する
     */
    @Query(value = "SELECT * FROM users WHERE users.deleted = true", nativeQuery = true)
    List<Users> findByDeleted();
}

/*
 * Repositoryのインターフェースを作成
 * Spring Data JPAのJpaRepositoryを継承することで、自動的にCRUD処理を実装できる。
 * 
 * JpaRepositoryが提供するメゾット(デフォで使える)有能！
 * findAll() 全件取得する
 * findById([データ型] [テーブルの名前]) 例）findById(int id)
 * save(User user) 新規登録または更新する
 * count() 全部の件数を取得する
 */
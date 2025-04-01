package com.kento.springprofilewebapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.kento.springprofilewebapp.model.Users;

public interface UserRepository extends JpaRepository<Users, Integer>{
    Optional<Users> findByEmail(String email); // カスタムメゾット、メールアドレスで検索

    Page<Users> findAll(Pageable pageable); // ユーザー一覧の件数を制限して取得する

    // ユーザーに対して削除フラグを付与する(削除するときに呼び出す物)
    @Transactional
    default void softDelete(int id) { // idで対象ユーザを選択する
        findById(id).ifPresent(user -> {
            user.setDeleted(true); // 削除フラグを更新する
            save(user); // 情報をDBに保存する
        });
    }

    /*
     * 現状モデル(エンティティ)で削除フラグを除外している為、削除フラグが立っているユーザは表示されない。
     * そのため、削除フラグがついているユーザも含んで検索する場合は、SQL文を用いて、カスタムで実装する
     */
    @Query(value = "SELECT * FROM users WHERE users.deleted = true", nativeQuery = true) // DB上のテーブルではなく、エンティティ(Users)を指定すること
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
package com.kento.springprofilewebapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.kento.springprofilewebapp.model.Inquirys;

public interface InquiryRepository extends JpaRepository<Inquirys, Integer>{
    // usersテーブルのidカラムを内部結合する。(users.id)
    // usersがnullの場合は取得が出来ない
    @Query("SELECT i FROM Inquirys i JOIN i.users u")
    List<Inquirys> findAllWithUsers();

    // usersをleftjoinして結合しつつ、一覧を取得
    @Query("select i from Inquirys i left join i.users u")
    List<Inquirys> findAllWithUsers2();

    // categorysテーブルのidカラムを内部結合する(categorys.id)
    @Query("SELECT i FROM Inquirys i JOIN i.categorys n")
    List<Inquirys> findAllWithCategorys();

    // 指定したIDのレコードを削除する
    void deleteById(int id);
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
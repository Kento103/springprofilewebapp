package com.kento.springprofilewebapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kento.springprofilewebapp.model.Users;

public interface UserRepository extends JpaRepository<Users, Integer>{
    Optional<Users> findByEmail(String email); // カスタムメゾット、メールアドレスで検索
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
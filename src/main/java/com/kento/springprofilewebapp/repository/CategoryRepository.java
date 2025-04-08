package com.kento.springprofilewebapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kento.springprofilewebapp.model.Categorys;

public interface CategoryRepository extends JpaRepository<Categorys, Integer>{
    // 指定したIDのレコードを削除する
    void deleteById(int id);
}

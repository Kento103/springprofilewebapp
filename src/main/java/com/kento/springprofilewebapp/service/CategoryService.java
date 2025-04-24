package com.kento.springprofilewebapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kento.springprofilewebapp.model.Categorys;
import com.kento.springprofilewebapp.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
    @Autowired
    private final CategoryRepository categoryRepository;

    // 全リストを取得する
    public List<Categorys> getAllLists() {
        return categoryRepository.findAll();
    }

    // カテゴリをIDで取得する
    public Categorys getCategorysById(Integer id) {
        return categoryRepository.findById(id).orElse(null);
    }

    // カテゴリ登録する
    public Categorys registeCategorys(String name) {
        Categorys categorys = new Categorys(name); // 取得する
        return categoryRepository.save(categorys); // 取得したものをモデルに挿入して、DBに保存する
    }

    // カテゴリ情報を編集する
    public Categorys updateCategorys(int id, Categorys categorys) {
        return categoryRepository.findById(id)
            .map(category -> {
                category.setName(categorys.getName()); // カテゴリ名
                return categoryRepository.save(category); // saveでDBの情報を更新する
            })
            .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));
    }
    // 指定したIDのレコードを削除する
    public void deleteCategory(int id) {
        categoryRepository.deleteById(id); // 指定した物を削除
    }
}

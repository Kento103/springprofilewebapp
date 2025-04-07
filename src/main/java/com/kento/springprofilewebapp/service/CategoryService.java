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
}

package com.kento.springprofilewebapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kento.springprofilewebapp.model.Inquirys;
import com.kento.springprofilewebapp.repository.InquiryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InquiryService {
    @Autowired
    private final InquiryRepository inquiryRepository;

    // 全リストを取得する
    public List<Inquirys> getAllLists() {
        return inquiryRepository.findAll();
    }

    // ユーザーをIDで取得する
    public Inquirys getInquirysById(Integer id) {
        return inquiryRepository.findById(id).orElse(null);
    }

    // users.idテーブルと結合させて表示する。
    public List<Inquirys> getInquirysWithUser() {
        return inquiryRepository.findAllWithUsers();
    }

    // categorysテーブルのidカラムを内部結合する
    public List<Inquirys> getInquirysWithCategorys() {
        return inquiryRepository.findAllWithCategorys();
    }

    // お問い合わせ登録する
    public Inquirys registeInquiry(String description) {
        Inquirys inquirys = new Inquirys(description); // データを取得する
        return inquiryRepository.save(inquirys); // 取得したものをモデルに挿入して、DBに保管する
    }
}

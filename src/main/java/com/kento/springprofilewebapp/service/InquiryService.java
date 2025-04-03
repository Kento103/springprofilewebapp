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
}

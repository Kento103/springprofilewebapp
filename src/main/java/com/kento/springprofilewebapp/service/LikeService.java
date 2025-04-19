package com.kento.springprofilewebapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kento.springprofilewebapp.model.Likes;
import com.kento.springprofilewebapp.repository.LikeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LikeService {
    @Autowired
    private final LikeRepository likeRepository;

    // 全リストの取得
    public List<Likes> getAllLists() {
        return likeRepository.findAll();
    }
}

package com.kento.springprofilewebapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kento.springprofilewebapp.model.Likes;

public interface LikeRepository extends JpaRepository<Likes, Integer>{
    // カスタム
}

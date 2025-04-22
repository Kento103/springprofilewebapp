package com.kento.springprofilewebapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kento.springprofilewebapp.model.Likes;
import com.kento.springprofilewebapp.model.Users;
import com.kento.springprofilewebapp.repository.LikeRepository;
import com.kento.springprofilewebapp.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LikeService {
    @Autowired
    private final LikeRepository likeRepository;

    @Autowired
    private final UserRepository userRepository;

    // 全リストの取得
    public List<Likes> getAllLists() {
        return likeRepository.findAll();
    }

    // データの保存(いいねを保存する)
    public Likes likeYou(int fromUserId, int toUserId) {
        Likes likes = new Likes();
        /*
         * Likes.javaでfromLikeUserIdおぴょび、toLikeUserIdはUsersがたとなっている。
         * int型を直接渡すと、型が違うため、エラーとなる。
         * そのため、Usersオブジェクトに変換してから、渡す必要がある。
         */
        // Usersオブジェクトに渡してやる。
        Users fromUser = userRepository.findById(fromUserId).orElseThrow(); // .orElseThrowはユーザがみつからなかった時の例外
        Users toUser = userRepository.findById(toUserId).orElseThrow();
        // 変換後、挿入する
        likes.setFromLikeUserId(fromUser); // いいねしたユーザー(ログイン中のユーザ)
        likes.setToLikeUserId(toUser); // いいねされるユーザ(対象のユーザー)
        return likeRepository.save(likes);
    }
}

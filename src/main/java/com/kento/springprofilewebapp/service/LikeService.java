package com.kento.springprofilewebapp.service;

import java.time.LocalDateTime;
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
    /*
     * 注意！非同期処理(@Async)を実装する場合は、必ず返り値の型をvoid,Future<T>,CompletableFuture<T>とする必要がある
     * 例）public void method() {}...結果を使わず、非同期で保存のみを行いたい場合(返り値なし)
     * public Future<Likes> method() {}
     * public CompletableFuture<Likes> method() {}...結果(Likesなど)を受け取りたい(返り値あり)
     * 等にすること
     */
    // 非同期処理(データベースの保存等、重めの処理を非同期で行うことができるアノテーション)
    public void likeYou(int fromUserId, int toUserId) {
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
        likeRepository.save(likes);
    }

    // 特定のユーザーのいいねされた数のカウント
    /**
     * 特定のユーザのいいねされた数のカウントをします。
     * @param id 検索対象のユーザID
     * @return いいねされた数を返します。
     */
    public int likesCount(int id) {
        return likeRepository.likesCount(id);
    }

    // 指定した年から現在までに取得したいいねのカウントを表示します
    public int likesCountYearAgo(int id, long fromYear) {
        LocalDateTime yearAgo = LocalDateTime.now().minusYears(fromYear); // 指定した年からのデータを取得する
        return likeRepository.likesCountPast(id, yearAgo); // 指定した年以降のものを検索して、その該当の件数を返す。
    }

    // 指定した月から現在までに取得したいいねのカウントを表示する
    public int likesCountMonthAgo(int id, long fromMonth) {
        LocalDateTime monthAgo = LocalDateTime.now().minusMonths(fromMonth); // 指定した月以降のデータを取得する
        return likeRepository.likesCountPast(id, monthAgo); // 指定した月以降の物を検索して、その該当の件数を返す。
    }

    /**
     * 対象のユーザがすでにいいねしているか確認します。(主にいいねしたかの確認に用います。)
     * 検索したユーザーがいいねをすでにしていたらtrueを、していなかったらfalseを返します。
     * @param loginId いいねを送った人(通常はログインしているユーザのIDを検索対象にする)
     * @param id いいねを送られた人(通常は表示中のページの対象ユーザ)
     * @return すでにいいねされている場合はtrueを、まだいいねしたことのない場合はfalseを返します。
     */
    public boolean isExistLike(int loginId, int id) {
        if (likeRepository.existByLike(loginId, id)) {
            // 存在する場合の処理
            return true;
        } else {
            // 存在しない場合の処理
            return false;
        }
    }

    /**
     * 指定したレコードを検索して削除します。返り値はありません。
     * @param loginId 対象のいいねを送ったユーザ
     * @param id 対象のいいねされたユーザ
     */
    public void unLikeYou(int loginId, int id) {
        likeRepository.removeLike(loginId, id);
    }
}

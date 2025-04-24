package com.kento.springprofilewebapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.kento.springprofilewebapp.model.Likes;

public interface LikeRepository extends JpaRepository<Likes, Integer>{
    /**
     * 特定のIDでいいねされた数をカウントします。(いいねされた数を調べるのに便利です。)
     * @param id 検索対象のユーザーID
     * @return いいねされた数が返ります。
     */
    @Query(value = "SELECT count(*) FROM likes where likes.to_like_id = :id", nativeQuery = true)
    int likesCount(@Param("id") int id);

    // すでにいいねを送っているか確認する
    /**
     * ユーザがすでにいいねを送信しているか確認します。(bool)
     * @param loginId 対象のいいねを送った人を検索する
     * @param id 対象のいいねした人を検索する
     * @return レコードが存在する場合はtrueを、存在しない場合はfalseを返します。
     */
    /* select * ではLikesエンティティの1レコードを返す想定で作成されている。そのため、jpaがboolに変換できず、エラーが発生する。
     * そのため、count(*) > 0 を用いてbooleanを返すようにする
     * また、データベースによってcount(*) > 0 の結果が1として返す場合もある(boolなのに勝手にintで返してしまうDBがある。)やはりboolに変換できずにエラーが発生する。
     * このエラーはJPQL(nativeQuery = false)にすることで違いを吸収するため解決する
     */
    @Query(value = "SELECT count(1) > 0 FROM Likes l WHERE l.fromLikeUserId.id = :loginId AND l.toLikeUserId.id = :id", nativeQuery = false)
    boolean existByLike(@Param("loginId") int loginId, @Param("id") int id);

    // 指定したデータを削除(いいね取り消し用)
    /**
     * 指定したレコードを削除します。いいね取り消しの際に使います。
     * @param loginId 対象のいいねを送った人を検索
     * @param id 対象のいいねをした人を検索
     */
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Likes l WHERE l.fromLikeUserId.id = :loginId AND l.toLikeUserId.id = :id", nativeQuery = false)
    void removeLike(@Param("loginId") int loginId, @Param("id") int id);
}

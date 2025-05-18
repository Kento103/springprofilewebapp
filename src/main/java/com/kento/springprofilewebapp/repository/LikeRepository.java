package com.kento.springprofilewebapp.repository;

import java.time.LocalDateTime;
import java.util.List;

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

    /**
     * 特定のIDで期間内でいいねされた数をカウントします
     * @param id 検索対象のユーザーID
     * @param past 対象の開始日付
     * @return 開始日付から現在までにいいねされた数が返ります
     */
    @Query(value = "select count(*) from likes l where l.to_like_id = :id and l.liked_at >= :past", nativeQuery = true)
    int likesCountPast(@Param("id") int id, @Param("past") LocalDateTime past);

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

    // いいねが多い順番に並び替える
    @Query(value = "select u.id, u.username, count(l.id) as like_count from users u left join likes l on u.id = l.to_like_id group by u.id, u.username order by like_count desc", nativeQuery = true)
    List<Likes> rankLike();
}

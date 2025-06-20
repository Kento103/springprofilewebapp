package com.kento.springprofilewebapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.kento.springprofilewebapp.model.Users;

public interface UserRepository extends JpaRepository<Users, Integer>{
    Optional<Users> findByEmail(String email); // カスタムメゾット、メールアドレスで検索

    Page<Users> findAll(Pageable pageable); // ユーザー一覧の件数を制限して取得する

    // ユーザーに対して削除フラグを付与する(削除するときに呼び出す物)
    /**
     * ユーザーに削除フラグを立て、論理削除します。このメゾットを実行しても、データベースからは削除されません。
     * @param id 論理削除したいユーザーID
     */
    @Transactional
    default void softDelete(int id) { // idで対象ユーザを選択する
        findById(id).ifPresent(user -> {
            user.setDeleted(true); // 削除フラグを更新する
            user.setDeletedAt(LocalDateTime.now()); // 現在時刻で記録
            user.setUpdateAt(LocalDateTime.now()); // 現在時刻で更新
            save(user); // 情報をDBに保存する
        });
    }

    // ユーザに対して削除フラグを取り消す(復元するときに呼び出す物)
    // NotUsed(@Whereを使っているので使えない。)
    @Transactional
    @Modifying
    default void softRecovery(int id) {
        findById(id).ifPresent(user -> { // idで対象ユーザーを選択する
            user.setDeleted(false); // 削除フラグを取り消し更新する
            user.setUpdateAt(LocalDateTime.now()); // 更新時間を更新
            save(user); // 情報をDBに保存する
        });
    }

    // ユーザーに対してロック状態をつける
    @Transactional
    @Modifying // update分とinsert分はこの2つのアノテーションがいる！！
    default void accountLock(int id) {
        findById(id).ifPresent(user -> {
            user.setLocked(true); // ロックする
            user.setUpdateAt(LocalDateTime.now()); // 現在時刻で記録
            save(user);
        });
    }

    // ユーザに対してロックを解除する
    @Transactional
    default void accoutUnLock(int id) {
        findById(id).ifPresent(user -> {
            user.setLocked(false); // ロックを解除する
            user.setUpdateAt(LocalDateTime.now()); // 現在時刻で記録
            save(user);
        });
    }

    /*
     * 本来は上記の削除フラグを付与する方式のfalseで削除フラグを取り消し(false)にしたい。
     * しかし、モデルにて@Whereを入力してしまってるため、削除フラグがついているものは、検索出来ない。
     * そのため、直接クエリ文を記載し、フラグを書き換える。
     * メゾットはvoid型でも問題ないが、int型にすると更新件数が戻ってくる
     * // 例) int recoveryUser(@Param("id") int id); // 更新件数が返ってくる
     */
    /**
     * 論理削除したユーザーの削除フラグを無効にし、復元するためのメゾットです。
     * @param id 復元対象のユーザーID
     */
    @Transactional // @Modifyingを使うメゾットはデフォルトでトランザクションが適用されないため、@Transactionalアノテーションをつけてデータ変更を確実に反映させる
    @Modifying // UPDATEやDELETEなどのデータ変更クエリを実行する際、@Modifyingを追加する必要がある
    @Query(value = "UPDATE users SET users.deleted = FALSE, users.deleted_at = null WHERE users.id = :id", nativeQuery = true)
    void recoveryUser(@Param("id") int id); // @Paramを明示的につけて、クエリパラメータを確実にわたるようにする

    /**
     * ユーザー情報を削除(完全削除)する為のメゾットです。このメゾットを実装するとユーザのレコードは完全に消去されます。
     * @param id 削除対象のidを入力
     */
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM users WHERE users.id = :id", nativeQuery = true)
    void removeUser(@Param("id") int id);

    /*
     * 現状モデル(エンティティ)で削除フラグを除外している為、削除フラグが立っているユーザは表示されない。
     * そのため、削除フラグがついているユーザも含んで検索する場合は、SQL文を用いて、カスタムで実装する
     */
    @Query(value = "SELECT * FROM users WHERE users.deleted = true", nativeQuery = true)
    List<Users> findByDeleted();

    /*
     * 削除されているユーザの人数を検索する
     */
    @Query(value = "select count(id) from users where users.deleted = true", nativeQuery = true)
    long countByDeletedUser();

    /*
     * 権限の変更をする
     */
    @Transactional
    @Modifying
    @Query("update Users u set u.role = :grant where u.id = :id")
    void changeUserGrant(@Param("id") int id, String grant);

    // likesテーブルと内部結合し、月間いいねの多い順にユーザを並び替える
    @Query(value = "select u.id as user_id, u.username, u.description, u.image_path, count(l.id) as total_likes from users u left join likes l on l.to_like_id = u.id and l.liked_at >= now() - interval 1 month where deleted = false and u.role = 'ROLE_USER' group by u.id, u.username order by count(l.id) desc, u.id asc", nativeQuery = true)
    List<UserLikeSummary> sortByMostLikes(); // 本来Usersで取得したいが、idが競合しjpaでエラーになる。そのため、asでサマリーを作成するが、サマリーを作成する場合はinterfaceを作成する必要がある。そのためUserLikeSummaryで取得する方式となる

    // likesテーブルと内部結合し、年間いいねの多い順にユーザを並び替える
    @Query(value = "select u.id as user_id, u.username, u.description, u.image_path, count(l.id) as total_likes from users u left join likes l on l.to_like_id = u.id and l.liked_at >= now() - interval 12 month where deleted = false and u.role = 'ROLE_USER' group by u.id, u.username order by count(l.id) desc, u.id asc", nativeQuery = true)
    List<UserLikeSummary> sortByMostLikesYears(); // 本来Usersで取得したいが、idが競合しjpaでエラーになる。そのため、asでサマリーを作成するが、サマリーを作成する場合はinterfaceを作成する必要がある。そのためUserLikeSummaryで取得する方式となる

    // ユーザ一覧を表示する(ユーザロール限定)
    @Query(value = "select * from users u where u.role = 'ROLE_USER' and u.deleted = false order by u.id asc", nativeQuery = true)
    List<Users> usersList(); // 管理者ロール以外を指定している

    boolean existsByEmail(String email);
}

/*
 * Repositoryのインターフェースを作成
 * Spring Data JPAのJpaRepositoryを継承することで、自動的にCRUD処理を実装できる。
 * 
 * JpaRepositoryが提供するメゾット(デフォで使える)有能！
 * findAll() 全件取得する
 * findById([データ型] [テーブルの名前]) 例）findById(int id)
 * save(User user) 新規登録または更新する
 * count() 全部の件数を取得する
 * existById(userId) 存在チェックができる。 例) existsById(userId)
 */
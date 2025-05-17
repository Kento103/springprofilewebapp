package com.kento.springprofilewebapp.model;


import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTOです！
// データベースのテーブルに対応するエンティティを作成
@Entity
@Table(name = "likes") // テーブル名
@NoArgsConstructor
@AllArgsConstructor
@Data // ゲッターセッターを自動で生成
public class Likes {
    @Id // しゅきー！！(Likeだけに)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自動採番(AutoInq)を設定する。これにより、IDは自動設定される。
    private int id; // 管理ID(PK、NN、UQ)

    @ManyToOne
    @JoinColumn(name = "from_like_id")
    @NotNull
    private Users fromLikeUserId; // いいね！した本人(誰からいいねをもらったか確認する為にある)

    @ManyToOne
    @JoinColumn(name = "to_Like_id")
    @NotNull
    private Users toLikeUserId; // いいね！を誰に押したか確認する為の物(いいね！をいくつもらえたかの確認にも使える)

    private LocalDateTime likedAt = LocalDateTime.now(); // いいねされた時間を現在時刻で記録する
}

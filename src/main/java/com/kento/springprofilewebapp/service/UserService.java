package com.kento.springprofilewebapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import com.kento.springprofilewebapp.model.Users;
import com.kento.springprofilewebapp.repository.UserLikeSummary;
import com.kento.springprofilewebapp.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MBまで

    // 全ユーザを取得する
    public List<Users> getallUsers() {
        return userRepository.findAll();
    }

    // 全ユーザ(一般ユーザのみ)を取得する
    public List<Users> getAllNormalUsers() {
        return userRepository.usersList();
    }

    // ユーザーをIDで取得
    public Users getUserById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    // ユーザー一覧を月間いいねの多い順に取得する
    public List<UserLikeSummary> getMostLikeUsers() {
        return userRepository.sortByMostLikes();
    }

    // ユーザ一覧を年間いいねの多い順に取得する
    public List<UserLikeSummary> getMostYearsLikeUsers() {
        return userRepository.sortByMostLikesYears();
    }

    // ユーザー情報を保存する(基本的な保存)
    public void save(Users users) {
        userRepository.save(users);
    }
    
    // ユーザー登録する(アクセス制御はstringで入れてください 0...許可、1...禁止)
    public Users registerUser(String username, String email, String password, String hurigana, String description, int sexial, String role, String inputAge, String locked, String imagePass) {
        String encordedPassword = passwordEncoder.encode(password); // パスワードをハッシュ化する
        boolean islocked; // アクセス制御の挿入用
        if (locked.equals("0")) {
            islocked = false; // アクセス許可
        } else if (locked.equals("1")) {
            islocked = true; // アクセス禁止
        } else {
            System.out.println("アクセス制御に渡されたパラメータが正しくありません。確認してください\n渡されたパラメータ：" + locked);
            islocked = false; // アクセス許可
        }
        Users user = new Users(username, email, encordedPassword, role, hurigana, description, sexial, this.checkInputAge(inputAge), islocked, imagePass); // パスワードはハッシュ化して、ロールはユーザーで保管する
        user.setCreateAt(LocalDateTime.now()); // 現在時刻で登録
        return userRepository.save(user); // DBにユーザー情報を保管する
    }

    // パスワードのみ変更する
    public Users changePassword(int id, String password) {
        Users user = userRepository.findById(id).orElse(null);
        String encordedPassword = passwordEncoder.encode(password); // パスワードをハッシュかする
        user.setPassword(encordedPassword);
        user.setUpdateAt(LocalDateTime.now()); // 現在時刻で更新
        return userRepository.save(user); // DBにユーザ情報を保管する
    }

    // ユーザー情報を編集する
    public Users updateUser(int id, Users updatedUser) {
        return userRepository.findById(id) // ユーザーをIDで検索する
            .map(user -> { // #.mapでOptionalないのデータを取り出し、編集している
                user.setUsername(updatedUser.getUsername()); // ユーザーネームを取得して変数に代入する
                user.setHurigana(updatedUser.getHurigana()); // ふりがな
                user.setEmail(updatedUser.getEmail()); // 登録メールアドレス
                user.setSexial(updatedUser.getSexial()); // 性別
                user.setAge(updatedUser.getAge()); // 年齢
                user.setDescription(updatedUser.getDescription()); // 自己紹介
                user.setRole(updatedUser.getRole()); // 権限
                user.setLocked(updatedUser.isLocked()); // ロック状態(paramから取得するときは#.isLockedでよい)
                System.out.println("content = [" + user.getDescription().replace("\n", "\\n").replace("\r", "\\r") + "]"); // 試験用
                user.setUpdateAt(LocalDateTime.now()); // 現在時刻で更新
                return userRepository.save(user); // #.saveでデータベースの情報を更新する
            })
            .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません")); // 該当するユーザーが三つからない場合はこのエラーに遷移する
    }

    // 一般ユーザプロフィールの編集※設計しなおし
    public Users updateProfile(Users updateUser, String username, String hurigana, Integer sexial, Integer age, String description) {
        updateUser.setUsername(username);
        updateUser.setHurigana(hurigana);
        updateUser.setSexial(sexial);
        updateUser.setAge(age);
        updateUser.setDescription(description);
        return userRepository.save(updateUser);
    }

    // パスワードエンコードのみ
    public String passwordEncorded(String password) {
        String encordedPassword = passwordEncoder.encode(password);
        return encordedPassword; // ハッシュ化したパスワードを返す
    }

    // 最初の5件を取得する
    public List<Users> getLimitedUsers(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size)).getContent(); // 最初のページの5件を取得する
    }

    // テーブルの全レコード件数を取得する
    public long countUsers() {
        return userRepository.count();
    }

    // 指定したユーザーを削除フラグを立てる(論理削除)
    @Transactional
    public void deletedUser(int id) {
        userRepository.softDelete(id);
    }

    // 指定したユーザーの削除フラグを取り消す(論理削除取り消し)
    @Transactional
    public void recoveryUser(int id) {
        userRepository.recoveryUser(id);
    }

    // 削除フラグ(論理削除)がついているユーザの一覧を表示する
    @Transactional
    public List<Users> deleted_list() {
        return userRepository.findByDeleted(); // 削除済みユーザを表示する
    }

    // 削除されているユーザの人数を確認
    public long countDeletedList() {
        return userRepository.countByDeletedUser(); // 人数をlongで返す
    }

    // 指定したユーザーを完全に削除する(物理削除)
    @Transactional
    public void removeUser(int id) {
        userRepository.removeUser(id);
    }

    // 指定したユーザの権限変更をする
    @Transactional
    public void changeGrant(int id, String grantName) {
        userRepository.changeUserGrant(id, grantName);
    }

    // 指定したユーザのロック状態の変更をする
    @Transactional
    public void changeLock(int id) {
        userRepository.accountLock(id);
    }

    // 指定したユーザのロック状態を解除する
    @Transactional
    public void changeUnLock(int id) {
        userRepository.accoutUnLock(id);
    }

    // 画像ファイルアップロード時のバリデーションチェック
    public void imageValidate(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new IllegalArgumentException("ファイルが空です。");
        }
        String contentType = multipartFile.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("画像ファイルではありません");
        }
        if (multipartFile.getSize() > MAX_FILE_SIZE) {
            throw new MaxUploadSizeExceededException(MAX_FILE_SIZE);
        }
    }

    // 確認用削除していないユーザが0人の場合、trueとなる
    public boolean isActiveUsers() {
        if (userRepository.count() == 0) {
            return true;
        } else {
            return false;
        }
    }

    // 権限チェッカー(権限チェックOK...true、権限チェックNG...false)
    /**
     * ログイン中のユーザと対象のユーザのUserIDを比較し、権限に対し、編集する資格があるか確認します<br>
     * 資格がある場合はtrueを、資格がない場合はfalseを返します。<br>
     * 必ず両方の引数に入れてください。(どちらかしか入ってない場合はは資格なしと返します。)
     * @param user 対象のユーザ(Usersクラス)
     * @param loginUser 現在ログイン中のユーザ(Usersクラス)
     * @return 資格がある場合はtrueを、資格がない場合はfalseを返します
     */
    public boolean chackGrant(Users user, Users loginUser) {
        if (user == null || loginUser == null) {
            // そもそもどちらかがnull(引数入ってない場合はNGとする)
            return false;
        }
        if (loginUser.getRole().equals("ROLE_ADMIN")) {
            // 管理者権限ロールでログイン中の場合はどのユーザの編集もできる。
            return true;
        } else {
            // 一般ユーザの場合の処理がここから始まるよ
            if (user.getId() == loginUser.getId()) {
                // 対象ユーザIDとログイン中のユーザIDが同じならOK
                return true;
            } else {
                // 違う場合はNG
                return false;
            }
        }
    }

    // 年齢入力値チェック(String>Integer)
    public Integer checkInputAge(String inputAge) {
        // 数値検査
        try {
            Integer age = Integer.parseInt(inputAge);
            return age; // 変換成功した場合は数値に変換して返す
        } catch (Exception e) {
            return null; // 変換失敗した場合はnullを返す
        }
    }
}

package com.kento.springprofilewebapp.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kento.springprofilewebapp.model.Users;
import com.kento.springprofilewebapp.service.LikeService;
import com.kento.springprofilewebapp.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Controller // これは！コントローラ！！！
@RequestMapping("/users")  // マッピング！
@RequiredArgsConstructor // コンストラクタを自動で入れてくれる物！
public class UserController {
    private final UserService userService; // 定数にしておくよ！
    private final LikeService likeService; // 定数にしておく。いいね昨日のコントロール用

    // 各ユーザーページを表示する
    @GetMapping("/{id}")
    public String getUserPage(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        Users user = userService.getUserById(id); // usersテーブルのidから該当の情報尾を検索する
        if (user == null) {
            // 存在しないユーザをたどった場合は、一覧に戻す
            redirectAttributes.addFlashAttribute("systemWarning", "指定したユーザは存在しません");
            return "redirect:/";
        } else if (user.getRole().equals("ROLE_ADMIN")) {
            // 管理者ユーザを検索しようとした場合は。一覧に戻す
            redirectAttributes.addFlashAttribute("systemWarning", "指定したユーザは管理者です");
            return "redirect:/";
        }
        model.addAttribute("user", user); // 情報をモデルへ代入する(thymaleefで使えるようにする)
        model.addAttribute("like", likeService.likesCount(id)); // いいねされた数をカウントする。(総数)
        model.addAttribute("like_year", likeService.likesCountYearAgo(id, 1)); // いいねされた数をカウントする(1年前から現在)
        model.addAttribute("like_month", likeService.likesCountMonthAgo(id, 1)); // いいねされた数をカウントする(1カ月まえから現在)
        model.addAttribute("systemError", (String) model.getAttribute("systemError"));
        model.addAttribute("systemSuccess", (String) model.getAttribute("systemSuccess"));
        return "profile"; // profile.htmlのページを表示する
    }

    // 各ユーザーページを表示する(旧コード将来のために無効化して残す)
    // @GetMapping("/{id}")
    // public String getUserPage(@PathVariable int id, Model model, @AuthenticationPrincipal Users loginUser, RedirectAttributes redirectAttributes) {
    //     Users user = userService.getUserById(id); // usersテーブルのidから該当の情報尾を検索する
    //     if (user == null) {
    //         // 存在しないユーザをたどった場合は、一覧に戻す
    //         redirectAttributes.addFlashAttribute("systemWarning", "指定したユーザは存在しません");
    //         return "redirect:/";
    //     }
    //     model.addAttribute("user", user); // 情報をモデルへ代入する(thymaleefで使えるようにする)
    //     model.addAttribute("like", likeService.likesCount(id)); // いいねされた数をカウントする。(総数)
    //     model.addAttribute("like_year", likeService.likesCountYearAgo(id, 1)); // いいねされた数をカウントする(1年前から現在)
    //     model.addAttribute("like_month", likeService.likesCountMonthAgo(id, 1)); // いいねされた数をカウントする(1カ月まえから現在)
    //     model.addAttribute("isLike", likeService.isExistLike(loginUser.getId(), id)); // すでに言い値しているかの有無を確認する。boolで返るためこれをthymaleef側で表示制御するのに用いる
    //     model.addAttribute("systemError", (String) model.getAttribute("systemError"));
    //     model.addAttribute("systemSuccess", (String) model.getAttribute("systemSuccess"));
    //     return "profile"; // profile.htmlのページを表示する
    // }

    // ユーザー情報の編集ページを表示する
    @GetMapping("/{id}/edit")
    public String editUserPage(@PathVariable int id, Model model, Users user, @AuthenticationPrincipal Users loginUser, RedirectAttributes redirectAttributes) {
        user = userService.getUserById(id); // usersテーブルのidから該当の情報を検索する
        if (user == null) {
            // 存在しないユーザをたどった場合は、一覧に戻す
            redirectAttributes.addFlashAttribute("systemWarning", "指定したユーザは存在しません");
            return "redirect:/";
        }
        model.addAttribute("user", user); // 情報をモデルへ代入する(thymaleefで使えるようにする)
        model.addAttribute("systemSuccess", (String) model.getAttribute("systemSuccess"));
        if (loginUser.getRole().equals("ROLE_ADMIN")) { // 自分がROLE_ADMIN保有者か確認するloginUserはオブジェクトなので#.equalsでないと不一致となる(==ではだめ)
            // 管理者権限ロールでログイン中の場合はどのユーザの編集もできる
            return "useredit"; // useredit.htmlのページを表示する
        } else {
            // それ以外のユーザは自分のみの編集ができる
            if (user.getId() == loginUser.getId()) { // user.getId()...閲覧中のUserID loginUser.getId()...ログイン中のユーザーID
                return "useredit"; // useredit.htmlのページを表示する
            } else {
                // リダイレクト先で呼び出すために用意する
                redirectAttributes.addFlashAttribute("systemError", "権限がありません");
                // model.addAttribute("error", "権限がありません");
                return "redirect:/users/{id}"; // プロフィールに戻す
            }
        }
    }

    // ユーザー情報の更新を行うもの
    @PostMapping("/{id}")
    public String updateUser(
            @PathVariable int id,
            @Valid @ModelAttribute("user") Users user,
            BindingResult bindingResult,
            Model model,
            @ModelAttribute Users users,
            @AuthenticationPrincipal Users loginUsers,
            RedirectAttributes redirectAttributes
            ) {
            // 値を上書きする
            user.setPassword(loginUsers.getPassword()); // パスワードを上書きして変更させない
            if (bindingResult.hasErrors()) {
                Users dbUser = userService.getUserById(id);
                user.setImagePath(dbUser.getImagePath()); // 画像が消えてしまうので保管する
                //Getリクエスト用のメゾットを呼び出し、編集画面に戻る
                model.addAttribute("systemError", "入力が正しくありません\n確認してください！");
                model.addAttribute("user", user);
                return "useredit";
                // return editUserPage(user.getId(), model, user, loginUsers, null);
            }
            if (!userService.chackGrant(users, loginUsers)) {
                redirectAttributes.addFlashAttribute("systemError", "不正なリクエストです\n権限がありません。");
                return "redirect:/{id}/edit";
            }
            try {
                users = userService.updateUser(id, user);
                model.addAttribute("user", users);
                redirectAttributes.addFlashAttribute("systemSuccess", "ユーザー情報の変更に成功しました");
                return "redirect:/admin";
            } catch (Exception e) {
                model.addAttribute("systemError", "登録に失敗しました");
                return "useredit";
            }
        }
    
    // ユーザーのプロフィール画像を変更するための画面
    @GetMapping("/{id}/edit/image")
    public String changeImage(@PathVariable int id, Model model) {
        Users users = userService.getUserById(id); // 対象のユーザーを検索
        model.addAttribute("user", users);
        model.addAttribute("systemError", (String) model.getAttribute("systemError"));
        model.addAttribute("systemSuccess", (String) model.getAttribute("systemSuccess"));
        return "useredit_image";
    }

    // プロフィール画像の変更を処理する(Postリクエスト)
    @PostMapping("/{id}/edit/image")
    public String updatePhoto(@PathVariable int id, @RequestParam("image") MultipartFile file, RedirectAttributes redirectAttributes, @AuthenticationPrincipal Users loginUsers) {
        try {
            Users user = userService.getUserById(id); // 対象のユーザーを検索
            if (!userService.chackGrant(user, loginUsers)) {
                redirectAttributes.addFlashAttribute("systemError", "不正なリクエストです\n権限がありません。");
                return "redirect:/{id}/edit";
            }
            String uploadDir = System.getProperty("user.dir") + "/uploads/"; // アップロードするディレクトリを指定する
            // ファイルのバリデーションチェックを行う
            userService.imageValidate(file);
            // 画像が設定されていた場合は、以前設定されていた画像ファイルをサーバから削除する
            String imagePath = user.getImagePath();
            if (imagePath != null) {
                System.out.println(user.getImagePath()); // 試験用
                System.out.println(user.getImagePath().substring(8)); // 先頭から8文字を削除する(ファイルパスが/images/のため)
                String deleteImageFilename = user.getImagePath().substring(8); // 先頭の/images/の文字列を削除する
                Path deletePath = Paths.get(uploadDir + deleteImageFilename); // 削除する画像のパスを指定する
                Files.delete(deletePath); // 変更前に設定されていた画像を削除する
            }
            String filename = "user_" + id + "_" + file.getOriginalFilename(); // ファイル名を指定する(file.getOriginalFilenameはファイル名をそのまま使用する為のもの)
            Path path = Paths.get(uploadDir + filename);
            Files.createDirectories(path.getParent()); // ディレクトリがない時は作成してくれる。
            file.transferTo(path.toFile());

            // データベースにデータを保管する
            user.setImagePath("/images/" + filename); // Webでアクセスするパスになる
            userService.save(user);
            redirectAttributes.addFlashAttribute("systemSuccess", "画像のアップロードに成功しました");
            return "redirect:/users/{id}/edit";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("systemError", "画像ファイルをアップロードしてください");
            return "redirect:/users/{id}/edit";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("systemError", "予期せぬエラーが発生しました");
            return "redirect:/users/{id}/edit";
        } catch (MaxUploadSizeExceededException e) {
            redirectAttributes.addFlashAttribute("systemError", "画像のファイルサイズは最大2MBまでです");
            return "redirect:/users/{id}/edit";
        }
        
    }

    // ユーザーのパスワード変更を行うページ
    // @GetMapping("/{id}/edit/password")
    // public String editPassword(@PathVariable int id, Model model) {
    //     Users users = userService.getUserById(id); // 対象のユーザーを検索
    //     model.addAttribute("user", users);
    //     return "useredit_password";
    // }

    // パスワード変更処理を行う為のページ
    @PostMapping("/{id}/edit/password")
    public String changePassword(@PathVariable int id, @RequestParam String password, Model model, RedirectAttributes redirectAttributes, @AuthenticationPrincipal Users loginUsers) {
        Users user = userService.getUserById(id); // 対象のユーザーを検索
        if (!userService.chackGrant(user, loginUsers)) {
                redirectAttributes.addFlashAttribute("systemError", "不正なリクエストです\n権限がありません。");
                return "redirect:/{id}/edit";
        }
        // パスワード専用バリデーションチェック(パスワードはハッシュ化するため、下の所でバリデーションチェック出来ない)
        // チェック用(正規表現)検査値
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_-]{8,32}$");
        // 正規表現被検査値
        Matcher checkPassword = pattern.matcher(password);
        // パスワードが正規表現に一致しているかチェックする
        if (checkPassword.matches()) {
            // パスワード正規表現Pass
            try {
                userService.changePassword(id, password);
                redirectAttributes.addFlashAttribute("systemSuccess", "パスワードを変更しました");
                return "redirect:/users/{id}";
            } catch (Exception e) {
                // model.addAttribute("error", "パスワードの保存中にエラーが発生しました！");
                redirectAttributes.addFlashAttribute("systemError", "パスワードの変更中にエラーが発生しました！");
                return "redirect:/users/{id}/edit";
            }
        } else {
            // パスワード正規表現fail
            // model.addAttribute("error", "パスワードは8～32文字かつ半角英数と数字、ハイフンアンダーバーが使用可です。");
            redirectAttributes.addFlashAttribute("systemError", "パスワードは8~32文字かつ、半角英数及びハイフン、アンダーバーが使用可能です");
            return "redirect:/users/{id}/edit";
        }
        
    }

    // ユーザーリスト一覧(ここは、usersのルートで表示したいので、@GetMappingの)引数は入れない！
    // そのうち抹消対象
    @GetMapping
    public String userList(Model model) {
        model.addAttribute("users", userService.getallUsers());
        model.addAttribute("systemWarning", (String) model.getAttribute("systemWarning"));
        return "userlist";
    }

    // ユーザーを指定したページから5件だけ取得する
    @GetMapping("/list")
    public String getUsers(
            @RequestParam(defaultValue = "0") int page, // URL?page=0でページを取得できる,defaultValueで指定しなかったときの初期値を指定できる。
            Model model) { // 取得したものを入れる用のもの
        model.addAttribute("users", userService.getLimitedUsers(page, 5)); // page:ページ数 size：いくつ取得するか
        return "userlist";
    }

    // いいねした時の動作(Postリクエスト)
    /**
     * (Postリクエスト)いいね！をする機能です。Likeテーブルにいいねした人といいねされた人の情報を登録、または編集します。
     * @param id いいねされる対象のユーザID(通常は自動で渡します。)
     * @param user ユーザIDから対象のユーザを検索します
     * @param loginUser 現在ログイン中のユーザがここに入ります。
     * @return DBに保存して、プロフィールページに遷移します。
     */
    @PostMapping("/{id}/like")
    public String likeYou(@PathVariable int id, Users user, Model model) {
        user = userService.getUserById(id); // URL中のidから該当のユーザを検索する。
        model.addAttribute("user", user);
        likeService.likeYou(1, id); // fromにはログイン中のユーザID、toには対象のユーザーIDが入る。いいねをして保存する
        return "redirect:/users/{id}";
    }

    // いいねする(ログインを確認してた時のコード)
    // @PostMapping("/{id}/like")
    // public String likeYou(@PathVariable int id, Users user, @AuthenticationPrincipal Users loginUser, Model model) {
    //     user = userService.getUserById(id); // URL中のidから該当のユーザを検索する。
    //     model.addAttribute("user", user);
    //     likeService.likeYou(loginUser.getId(), id); // fromにはログイン中のユーザID、toには対象のユーザーIDが入る。いいねをして保存する
    //     return "redirect:/users/{id}";
    // }


    // いいねをやめる(取り消し)の動作(Postリクエスト)
    /**
     * (Postリクエスト)いいねを取り消す機能です。Likeテーブルのレコード情報を削除します。
     * @param id いいねされたユーザID
     * @param user Userエンティティ
     * @param loginUser ログイン中のユーザID
     * @param model モデル
     * @return DBから削除してプロフィールページに遷移します。
     */
    @PostMapping("/{id}/unlike")
    public String unLikeYou(@PathVariable int id, Users user, @AuthenticationPrincipal Users loginUser, Model model) {
        user = userService.getUserById(id); // URL中のidから該当ユーザを検索する。
        model.addAttribute("user", user);
        likeService.unLikeYou(loginUser.getId(), id); // fromにはログイン中のユーザーid、toには対象のユーザIDが入る。該当のいいねのレコードを削除して保存する
        return "redirect:/users/{id}";
    }
}

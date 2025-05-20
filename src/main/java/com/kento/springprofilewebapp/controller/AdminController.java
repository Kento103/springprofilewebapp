package com.kento.springprofilewebapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kento.springprofilewebapp.model.Users;
import com.kento.springprofilewebapp.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller // コントローラ
@RequestMapping("/admin") // マッピング
@RequiredArgsConstructor // コンストラクタを自動で入れてくれるもの
public class AdminController {
    private final UserService userService;
    // 管理者ページトップ
    // @GetMapping
    // public String admintop() {
    //     return "admin";
    // }

    // 現在登録されているユーザーリストを表示する
    /**
     * 現在登録されているユーザリストを表示する為の物です5ユーザ毎に表示します
     * @param page 何ページ目を表示するかの引数です。指定しない場合は最初のページを表示します
     * @param allUser // ユーザ数を検索するものです。この引数は自動で入ります
     * @param model // thymeleef表示用の引数です。通常は代入不要です。
     * @return ユーザを検索し、userlist.htmlを表示します。
     */
    @GetMapping
    public String getUsers(
            @RequestParam(defaultValue = "0") int page, // URL?page=0でページを取得できる,defaultValueで指定しなかったときの初期値を指定できる。
            Model allUser, // ユーザー数を検索する(前ページ、次ページを出すために使うもの)
            Model model) { // 取得したものを入れる用のもの
        long maxPage = (userService.countUsers() - 1) / 5;
        model.addAttribute("users", userService.getLimitedUsers(page, 5)); // page:ページ数 size：いくつ取得するか
        allUser.addAttribute("allUsers", userService.countUsers()); // 全ユーザー人数確認用
        allUser.addAttribute("page", page); // 現在のページ
        allUser.addAttribute("maxpage", maxPage); // 最大ページ
        model.addAttribute("systemSuccess", (String) model.getAttribute("systemSuccess"));
        return "userlist";
    }

    // 削除されているユーザーリストを表示する
    /**
     * 削除されているユーザリストを表示します
     * @param model thymeleef表示用の引数です。通常は代入不要です。
     * @return 削除されているユーザリストを検索し、deleted_list.htmlを表示します
     */
    @GetMapping("/deleted_list")
    public String delUsers(Model model) { // 取得したものを入れるようの物
        model.addAttribute("users", userService.deleted_list());
        model.addAttribute("userscount", userService.countDeletedList()); // 削除ユーザの人数をlongで返す
        return "deleted_list";
    }

    // ユーザーを削除する(Postリクエスト)
    /**
     * id引数で指定したユーザを削除します
     * @param id 削除対象のユーザ
     * @param redirectAttributes thymeleef表示用の引数です。通常は代入不要です。
     * @return 指定したユーザを削除します。なおいない場合は実行しません。
     */
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable int id, RedirectAttributes redirectAttributes) {
        userService.deletedUser(id);
        redirectAttributes.addFlashAttribute("systemSuccess", "ユーザを削除しました。\n削除したユーザは削除ユーザ一覧から確認できます。");
        return "redirect:/admin";
    }

    // ユーザーを復元する(Postリクエスト)
    /**
     * id引数で指定したユーザを復元します。内部的には、削除フラグを取り消し(false)にします
     * @param id 復元対象のユーザ
     * @param redirectAttributes thymeleef表示用の引数です。通常は代入不要です。
     * @return 指定したユーザの削除フラグを取り消します。
     */
    @PostMapping("/{id}/recovery")
    public String recoveryUser(@PathVariable int id, RedirectAttributes redirectAttributes) {
        userService.recoveryUser(id);
        redirectAttributes.addFlashAttribute("systemSuccess", "選択したユーザを復元しました。");
        return "redirect:/admin/deleted_list";
    }

    // ユーザーを完全消去(物理削除)する(Postリクエスト)
    /**
     * 指定したユーザを完全に削除します。このメゾットを実行すると、DB側にdeleteのSQLを実行する為、注意してください
     * @param id 削除対象のユーザ
     * @param redirectAttributes thymeleef表示用の引数です。通常は代入不要です。
     * @return 指定したユーザのuser.idをwhereに指定し、deleteのsqlを実行します。
     */
    @PostMapping("/{id}/delete_all")
    public String deleteAllUser(@PathVariable int id, RedirectAttributes redirectAttributes) {
        userService.removeUser(id); // 完全削除コマンド。実行注意。
        redirectAttributes.addFlashAttribute("systemSuccess", "選択したユーザを完全に削除しました");
        return "redirect:/admin/deleted_list";
    }

    // ユーザのアクセス権限を変更する
    /**
     * 指定したユーザのアクセス権限を設定します。
     * @param id 権限の変更対象のユーザ
     * @param user 該当のユーザのモデル用変数です。代入不要
     * @param model thymeleef表示用の引数です。通常は代入不要です。
     * @param redirectAttributes thymeleef表示用の引数です。通常は代入不要です。
     * @return アクセス権限を変更します。(一般⇔管理者)
     */
    @PostMapping("/{id}/grant")
    public String changeGrant(@PathVariable int id, Users user, Model model, RedirectAttributes redirectAttributes) {
        user = userService.getUserById(id); // 該当のユーザを検索する
        if (user.getRole().equals("ROLE_USER")) {
            // 現在の権限がUSERの場合はADMINに変更する
            userService.changeGrant(id, "ROLE_ADMIN");
            redirectAttributes.addFlashAttribute("systemSuccess", "ユーザの権限を管理者に変更しました");
        } else if (user.getRole().equals("ROLE_ADMIN")) {
            // 現在の権限がADMINの場合はUSERに変更する
            userService.changeGrant(id, "ROLE_USER");
            redirectAttributes.addFlashAttribute("systemSuccess", "ユーザの権限を一般ユーザに変更しました");
        } else {
            // どれにも一致しない場合はなにもしない
            redirectAttributes.addFlashAttribute("systemWarning", "不正なリクエストです");
        }
        return "redirect:/admin";
    }

    // ユーザのアカウントロック状態を変更する
    /**
     * ユーザのアカウントのロック状態を変更します。(BANのフラグを有効にします)
     * @param id ロックする対象のユーザ
     * @param user 代入不要
     * @param model thymeleef表示用の引数です。通常は代入不要です。
     * @param redirectAttributes thymeleef表示用の引数です。通常は代入不要です。
     * @return 指定したユーザをロックします。
     */
    @PostMapping("{id}/locked")
    public String changeLock(@PathVariable int id, Users user, Model model, RedirectAttributes redirectAttributes) {
        user = userService.getUserById(id);
        System.out.println(user.isAccountNonLocked());
        if (user.isAccountNonLocked()) {
            // ロックされていないときはロックする
            userService.changeLock(id);
            redirectAttributes.addFlashAttribute("systemSuccess", "このユーザをロックしました");
        } else if (!user.isAccountNonLocked()) {
            // ロックされている時はロック解除する
            userService.changeUnLock(id);
            redirectAttributes.addFlashAttribute("systemSuccess", "このユーザのロックを解除しました");
        }
        return "redirect:/admin";
    }
}

package com.kento.springprofilewebapp.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kento.springprofilewebapp.model.Categorys;
import com.kento.springprofilewebapp.model.Inquirys;
import com.kento.springprofilewebapp.model.Users;
import com.kento.springprofilewebapp.service.CategoryService;
import com.kento.springprofilewebapp.service.InquiryService;
import com.kento.springprofilewebapp.service.MailService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("inquiry")
@RequiredArgsConstructor
public class InquiryController {
    private final InquiryService inquiryService;
    private final CategoryService categoryService;
    private final MailService mailService;

    // お問い合わせリスト
    /**
     * お問い合わせの一覧を表示します。
     * @param model thymeleef表示用の引数です。通常は代入不要です。
     * @param model2 thymeleef表示用の引数です。通常は代入不要です。
     * @return お問い合わせの一覧を表示します
     */
    @GetMapping
    public String inquiryTop(Model model, Model model2) {
        model.addAttribute("inquirys", inquiryService.getInquirysWithUser()); // userテーブル
        model2.addAttribute("categorys", inquiryService.getInquirysWithCategorys()); // categoryテーブル
        model.addAttribute("systemWarning", (String) model.getAttribute("systemWarning"));
        return "inquiry";
    }

    // お問い合わせ新規作成
    /**
     * お問い合わせを新規作成する画面を表示します
     * @param model thymeleef表示用の引数です。通常は代入不要です。
     * @return お問い合わせの作成画面を表示します
     */
    @GetMapping("/create")
    public String inquieyCreate(Model model) {
        List<Categorys> categorys = categoryService.getAllLists(); // カテゴリをDBから全件取得する
        model.addAttribute("categorys", categorys); // 取得したカテゴリリストをcategorysに入れる
        return "inquiry_create";
    }

    // お問い合わせ内容を送信する(Postリクエスト)
    /**
     * お問い合わせを新規作成します(sql>insert命令)
     * @param description 本文
     * @param category お問い合わせカテゴリ
     * @param email お問い合わせした人のメールアドレス
     * @param loginUser お問い合わせした人のuser.idを代入。nullの場合はビジター(ゲスト)が投稿したとみなします。
     * @param model thymeleef表示用の引数です。通常は代入不要です。
     * @param redirectAttributes thymeleef表示用の引数です。通常は代入不要です。
     * @return 登録し、メールを管理者宛を送信します
     */
    @PostMapping("/create")
    public String inquieyAdd(@RequestParam String description, @RequestParam int category, @RequestParam String email, @AuthenticationPrincipal Users loginUser, Model model, RedirectAttributes redirectAttributes) {
        try {
            // 登録成功したときの処理
            inquiryService.registeInquiry(description, category, loginUser, email);
            // リダイレクトする場合はRedirectAttributes#addFlashAttribteでパラメーターを送信できる
            // リダイレクト先で、Model#getAttributeで取り出す
            redirectAttributes.addFlashAttribute("systemSuccess", "お問い合わせの追加に成功しました");
            // model.addAttribute("systemSuccess", "お問い合わせの追加に成功しました");
            // 本文を設定する
            String mailBody = "新規のお問い合わせがありました。お問い合わせ内容は以下の通りです。\n\n" + description + "\n\nお問い合わせ管理のページを開いて、対応してください。";
            // メールの送信先(環境変数でtoを設定している)
            String mailTo = System.getenv("SPRING_MAIL_USERNAME");
            // メールを送信する
            mailService.sendMail(mailTo, "新規のお問い合わせがありました", mailBody);
            return "redirect:/";
        } catch (Exception e) {
            // 登録失敗したときの処理
            model.addAttribute("systemError", "お問い合わせの追加に失敗しました！内容を確認してください");
            return "inquiry_create";
        }
    }

    // 各お問い合わせ内容を表示する
    /**
     * お問い合わせの詳細画面を表示します
     * @param id 対象のお問い合わせID
     * @param model thymeleef表示用の引数です。通常は代入不要です。
     * @param redirectAttributes thymeleef表示用の引数です。通常は代入不要です。
     * @return 指定したお問い合わせを表示します
     */
    @GetMapping("/{id}")
    public String getInquiry(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        Inquirys inquirys = inquiryService.getInquirysById(id);
        if (inquirys == null) {
            redirectAttributes.addFlashAttribute("systemWarning", "指定したお問い合わせはありません");
            return "redirect:/inquiry";
        }
        model.addAttribute("inquiry", inquirys);
        return "inquiry_description";
    }

    // お問い合わせのステータスを更新する(Postリクエスト)
    /**
     * お問い合わせのステータスを変更します
     * @param id 変更対象のお問い合わせID(inquiry.id)
     * @param status 変更するフラグナンバー
     * @return 変更処理を行います。
     */
    @PostMapping("/{id}/update")
    public String updateInquiry(@PathVariable int id, @RequestParam int status) {
        System.out.println(id);
        System.out.println(status);
        Inquirys inquiry = inquiryService.getInquirysById(id);
        inquiry.setStatus(status);
        inquiryService.updateInquiry(inquiry);
        return "redirect:/inquiry";
    }

    // 質問者にメールを送信する(Postリクエスト)
    /**
     * 質問者にメールを送信します。送付先メールアドレス(to)はユーザがお問い合わせの際に登録したメールアドレスとなります
     * @param id 質問対象のお問い合わせid(inquiry.id)
     * @param emailBody 送信する予定の本文を記載
     * @param redirectAttributes thymeleef表示用の引数です。通常は代入不要です。
     * @return メールアドレス宛に本文の内容を送信します
     */
    @PostMapping("/{id}/send")
    public String sendEmail(@PathVariable int id, @RequestParam String emailBody, RedirectAttributes redirectAttributes) {
        try {
            if (emailBody.isEmpty()) {
                redirectAttributes.addFlashAttribute("systemError", "本文を入力してください！");
                return "redirect:/inquiry/{id}";
            }
            Inquirys inquiry = inquiryService.getInquirysById(id);
            String mailTitle = "お問い合わせ内容についてのご連絡";
            String mailBodyBegin = "ご利用いただき、ありがとうございます。\nお問い合わせ頂きました以下内容につきましてご回答致します。\n\n";
            String mailBodyFinalWord = "なお、ご不明な点がありましたら、お気軽にご連絡下さい。\n今後ともよろしくお願いいたします。";
            String mailBody = mailBodyBegin + "ご質問内容：" + inquiry.getDescription() + "\n\n" + "回答：" + emailBody + "\n\n" + mailBodyFinalWord;
            mailService.sendMail(inquiry.getInquiryEmail(), mailTitle, mailBody);
            redirectAttributes.addFlashAttribute("systemSuccess", "メールの送信に成功しました");
            return "redirect:/inquiry";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("systemError", "処理に失敗しました！");
            return "redirect:/inquiry/{id}";
        }
    }

    // お問い合わせを削除する
    /**
     * 指定したユーザのお問い合わせをを削除します
     * @param id 削除したい対象のお問い合わせID(inquiry.id)
     * @param redirectAttributes thymeleef表示用の引数です。通常は代入不要です。
     * @return 指定したレコードを検索し、削除します
     */
    @PostMapping("/{id}/delete")
    public String deleteInquiry(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            inquiryService.deleteInquiry(id);
            redirectAttributes.addFlashAttribute("systemSuccess", "削除しました");
            return "redirect:/inquiry";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("systemError", "処理に失敗しました！");
            return "redirect:/inquiry/{id}";
        }
    }

    // カテゴリーリストを表示する
    /**
     * カテゴリーリストを表示します
     * @param model thymeleef表示用の引数です。通常は代入不要です。
     * @return カテゴリーの一覧を表示します
     */
    @GetMapping("/category")
    public String categoryList(Model model) {
        model.addAttribute("categorys", categoryService.getAllLists());
        return "category_list";
    }

    // カテゴリーの追加(Getリクエスト)
    /**
     * カテゴリーの追加する画面を表示します
     * @param categorys バリデーション用です。代入不要
     * @param model thymeleef表示用の引数です。通常は代入不要です。
     * @return カテゴリーの追加画面を表示します
     */
    @GetMapping("/category/create")
    public String createCategory(Categorys categorys, Model model) {
        model.addAttribute("categorys", categorys);
        return "category_create";
    }

    // カテゴリの追加処理(Postリクエスト)
    /**
     * カテゴリを追加します
     * @param name 追加したいカテゴリ名
     * @param categorys バリデーション用
     * @param model thymeleef表示用の引数です。通常は代入不要です。
     * @param redirectAttributes thymeleef表示用の引数です。通常は代入不要です。
     * @return カテゴリーを追加します
     */
    @PostMapping("/category/create")
    public String registerCategory(@RequestParam String name, @ModelAttribute Categorys categorys, Model model, RedirectAttributes redirectAttributes) {
        try {
            // 登録成功した時の処理
            categoryService.registeCategorys(name);
            redirectAttributes.addFlashAttribute("systemSuccess", "カテゴリ登録に成功しました");
            return "redirect:/inquiry/category";
        } catch (Exception e) {
            model.addAttribute("systemError", "登録に失敗しました。入力内容を確認してください！");
            return "category_create";
        }
    }

    // カテゴリを削除する(Postリクエスト)
    /**
     * カテゴリーを削除します
     * @param id 指定したカテゴリIDのレコードを削除します
     * @return 該当のレコードを削除します
     */
    @PostMapping("/category/{id}/delete")
    public String deleteCategory(@PathVariable int id) {
        categoryService.deleteCategory(id);
        return "redirect:/inquiry/category";
    }

    // カテゴリを編集する
    /**
     * カテゴリ編集のページを表示します。
     * @param id 編集対象のカテゴリーを指定します
     * @param categorys バリデーション用です。代入不要
     * @param model thymeleef表示用の引数です。通常は代入不要です。
     * @return 編集ページを表示します
     */
    @GetMapping("/category/{id}/edit")
    public String editCategory(@PathVariable int id, Categorys categorys, Model model) {
        categorys = categoryService.getCategorysById(id);
        model.addAttribute("categorys", categorys);
        return "category_edit";
    }

    // 編集結果を保存する(Postリクエスト)
    /**
     * 編集内容を保存します
     * @param id 保存対象のユーザID
     * @param categorys バリデーション用です。代入不要
     * @param category バリデーション用です。代入不要
     * @param model thymeleef表示用の引数です。通常は代入不要です。
     * @return 保存します
     */
    @PostMapping("/category/{id}/edit")
    public String saveEditCategory(@PathVariable int id, @ModelAttribute Categorys categorys, @ModelAttribute Categorys category , Model model) {
        try {
            categorys =categoryService.updateCategorys(id, category);
            model.addAttribute("category", category);
            return "redirect:/inquiry/category";
        } catch (Exception e) {
            model.addAttribute("error" ,"登録に失敗しました。入力内容を確認してください");
            return "category_edit";
        }
    }

    // @GetMapping("/test")
    // public String inquiryTes(Model model) {
    //     model.addAttribute("inquirys", inquiryService.getInquirysWithUser());
    //     return "inquiry";
    // }
}

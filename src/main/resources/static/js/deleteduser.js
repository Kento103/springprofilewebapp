// 削除ユーザリストの完全削除フォーム
const deletedForm = document.getElementById('deletedform');
// 完全削除するボタン
const deletedButton = document.getElementById('deletedsubmit');

// 削除しようとする場合は確認をする
deletedForm.addEventListener('submit', function (event) {
    const confirmed = confirm("このユーザを完全削除します。\n完全削除すると復元することは出来ませんがよろしいですか？");

    if (!confirmed) {
        // いいえが選択された場合は、送信キャンセルする
        event.preventDefault();
    }
})
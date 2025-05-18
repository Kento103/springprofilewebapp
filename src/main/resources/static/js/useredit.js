// 画像入力用
const fileInput = document.getElementById('image');
// 画像保存用
const processButton = document.getElementById('saveButton');
// テキスト入力用
const passwordInput = document.getElementById('password');
// パスワード保存用
const passwordSubmit = document.getElementById('passwordsubmit');

// ファイルが選択されていないときは保存ボタンを押せないように制御する
fileInput.addEventListener('change', function () {
    if (fileInput.files.length > 0) {
        saveButton.disabled = false;
    } else {
        saveButton.disabled = true;
    }
});

// パスワードが入力されていないときはボタンを無効化する
passwordInput.addEventListener('input', function () {
    if (passwordInput.value.trim() !== '') {
       passwordsubmit.disabled = false;
    } else {
        passwordsubmit.disabled = true;
    }
})
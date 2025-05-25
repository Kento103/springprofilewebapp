function toggleContentRegister() {
    const value = document.querySelector('input[name="role"]:checked').value; // ラジオボタンを指定する
    if (!value) return; // 使えないときは何もしない(セーフコード)

    const admin = document.getElementById('content-admin');
    const user = document.getElementById('content-user');
    const userSexial = document.getElementById('content-sexial');
    const userAge = document.getElementById('content-age');
    const userDescription = document.getElementById('content-description');
    const inputAge = document.getElementById('age');
    const userImage = document.getElementById('content-image');
    const userHurigana = document.getElementById('content-hurigana');
    const inputHurigana = document.getElementById('hurigana');

    // 一旦すべて非表示にする
    admin.classList.add('hidden');
    user.classList.add('hidden');
    userSexial.classList.add('hidden');
    userAge.classList.add('hidden');
    userDescription.classList.add('hidden');
    userImage.classList.add('hidden');
    userHurigana.classList.add('hidden');

    // 選択に応じて表示する
    if (value === "ROLE_ADMIN") {
        admin.classList.remove('hidden');
        inputAge.value = '0';
        inputHurigana.value = 'かんりしゃ';
    } else if (value === "ROLE_USER") {
        user.classList.remove('hidden');
        userSexial.classList.remove('hidden');
        userAge.classList.remove('hidden');
        userDescription.classList.remove('hidden');
        userImage.classList.remove('hidden');
        userHurigana.classList.remove('hidden');
        inputAge.value = '';
        inputHurigana.value = '';
    }
}

function toggleAccess() {
    const value = document.querySelector('input[name="locked"]:checked').value;
    if (!value) return;

    const offLock = document.getElementById('content-unlocked');
    const onLock = document.getElementById('content-enablelocked');

    offLock.classList.add('hidden');
    onLock.classList.add('hidden');

    if (value === "0") {
        offLock.classList.remove('hidden');
    } else if ( value === "1") {
        onLock.classList.remove('hidden');
    }
}

// ページが読み込まれたら初期状態を設定する
document.addEventListener("DOMContentLoaded", function () {
    toggleContentRegister();
    toggleAccess();
})
function toggleContent() {
    const value = document.querySelector('input[name="rankradio"]:checked').value;
    if (!value) return;

    const month = document.getElementById('content-month');
    const year = document.getElementById('content-year');

    // 一旦すべて非表示にする
    month.classList.add('hidden');
    year.classList.add('hidden');

    // 選択に応じて表示
    if (value === "month") {
        month.classList.remove('hidden');
    } else if (value === "year") {
        year.classList.remove('hidden');
    }
}

// ページが読み込まれたら初期状態を設定する
document.addEventListener("DOMContentLoaded", function () {
    toggleContent();
});
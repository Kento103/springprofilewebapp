document.querySelectorAll('.like-btn').forEach(function(btn) {
    btn.addEventListener('click', function() {
    const userId = this.dataset.userId;
    fetch(`/addlike/${userId}`, { method: 'POST', credentials: 'include'})
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTPエラー ステータス: ${response.status}`) 
            }
            return response.json();
        })
        .then(likeCount => {
            // 同じuseridを持つすべてのカウント要素を置き換える方法
            document.querySelectorAll(`.like-count[data-user-id="${userId}"]`).forEach(function(span) {
                span.textContent = likeCount;
            });

            // 次に、カードのdata-like-countも更新する
            document.querySelectorAll(`.ranking-card[data-user-id="${userId}"]`).forEach(function(card) {
                card.setAttribute('data-like-count', likeCount);
            });

            // 最後に順番通りに並び替える
            sortRankingCards();

            // それぞれのボタンの近くのカウントを置き換える方法はこれ
            // const card = btn.closest('.card');
            // if (card) {
            //     const monthCount = card.querySelector('#like-month-count');
            //     if (monthCount) monthCount.textContent = likeCount;
            //     const yearCount = card.querySelector('#like-year-count');
            //     if (yearCount) yearCount.textContent = likeCount;
            // }

            // いっこだけ書き換える方法はこれ
            // document.getElementById('like-month-count').textContent = likeCount;
            // document.getElementById('like-year-count').textContent = likeCount;
        })
        .catch(e => console.error(e));
    });
});

// 並び替え関数
function sortRankingCards() {
    // 月間ランキング
    const containers = ['content-month', 'content-year'];
    containers.forEach(containerId => {
        const container = document.getElementById(containerId);
        if (!container) return;
        const cards = Array.from(container.querySelectorAll('.ranking-card'));
        cards.sort((a, b) => {
            return Number(b.getAttribute('data-like-count')) - Number(a.getAttribute('data-like-count'))
        });
        // 並び替え後にDOMを再配置する
        cards.forEach(card => container.appendChild(card));
        // 順位表示も更新する
        cards.forEach((card, idx) => {
            const rankSpan = card.querySelector('.rank-number');
            if (rankSpan) rankSpan.textContent = (idx + 1) + '位';
        });
    });
    // if (!container) return;
    // const cards = Array.from(container.querySelectorAll('.ranking-card'));
    // cards.sort((a, b) => {
    //     return Number(b.getAttribute('data-like-count')) - Number(a.getAttribute('data-like-count'));
    // });
    // // 並び替え後にDOMを再配置する
    // cards.forEach(card => container.appendChild(card));
}
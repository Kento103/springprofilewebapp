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
        })
        .catch(e => console.error(e));
    });
});

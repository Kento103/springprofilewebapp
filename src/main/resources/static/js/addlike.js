document.getElementById('like-btn').addEventListener('click', function() {
    const userId = this.dataset.userId;
    fetch(`/addlike/${userId}`, { method: 'POST', credentials: 'include'})
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTPエラー ステータス: ${response.status}`) 
            }
            return response.json();
        })
        .then(likeCount => {
            document.getElementById('like-count').textContent = likeCount;
            document.getElementById('like-month-count').textContent = likeCount;
            document.getElementById('like-year-count').textContent = likeCount;
        })
        .catch(e => console.error(e));
})
const deleteBtn = document.getElementById("deleteAccountBtn");
const homeBtn = document.getElementById("homeBtn");
const statusDiv = document.getElementById("status");


function showStatus(message, isSuccess = true) {
    if (!statusDiv) return;
    statusDiv.textContent = message;
    statusDiv.className = isSuccess ? 'success' : 'error';
    statusDiv.style.display = 'block';
    setTimeout(() => { statusDiv.style.display = 'none'; }, 2000);
}

deleteBtn?.addEventListener("click", async () => {
    if (!confirm("Вы точно хотите удалить аккаунт без возможности восстановления?")) return;

    try {
        const res = await fetch(`${contextPath}/account/delete`, {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" }
        });

        const data = await res.json();
        if (data.success) {
            showStatus("Аккаунт удалён! Перенаправление...", true);
            setTimeout(() => {
                window.location.href = contextPath + "/logout";
            }, 1500);
        } else {
            showStatus("Ошибка: " + (data.error || ""), false);
        }
    } catch (err) {
        console.error(err);
        showStatus("Ошибка сети", false);
    }
});

homeBtn?.addEventListener("click", () => {
    window.location.href = contextPath + "/home";
});

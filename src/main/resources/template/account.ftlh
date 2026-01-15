<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Аккаунт</title>
    <link rel="stylesheet" href="${contextPath}/static/css/account.css">
</head>
<body>

<div class="account-wrapper">
    <h1>Привет, ${username}!</h1>
    <button id="homeBtn">На главную</button>
    <button id="deleteAccountBtn" class="danger-btn">Удалить аккаунт</button>
</div>

<div id="status"></div>

<script>
    const contextPath = '${contextPath}';
</script>
<script src="${contextPath}/static/js/account.js"></script>
</body>
</html>

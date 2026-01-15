<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Вход</title>
    <link rel="stylesheet" href="${contextPath}/static/css/auth.css">
</head>
<body>
<div class="form-container">
    <h1>Вход</h1>

    <#if errormessage??>
        <div class="error-message">${errormessage}</div>
    </#if>

    <form action="${contextPath}/usercheck" method="post">
        <input type="text" name="username" placeholder="Имя пользователя" required>
        <input type="password" name="password" placeholder="Пароль" required>
        <input type="submit" value="Войти">
    </form>

    <div class="alternative">
        Нет аккаунта? <a href="${contextPath}/registration">Зарегистрироваться</a>
    </div>
</div>
</body>
</html>

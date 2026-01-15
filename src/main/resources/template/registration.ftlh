<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Регистрация</title>
    <link rel="stylesheet" href="${contextPath}/static/css/auth.css">
</head>
<body>
<div class="form-container">
    <h1>Регистрация</h1>

    <#if errormessage??>
        <div class="error-message">${errormessage}</div>
    </#if>

    <form action="${contextPath}/registration" method="post">
        <input type="text" name="username" placeholder="Имя пользователя" required>
        <input type="password" name="password" placeholder="Пароль" required>
        <input type="submit" value="Зарегистрироваться">
    </form>

    <div class="alternative">
        Уже есть аккаунт? <a href="${contextPath}/login">Войти</a>
    </div>
</div>
</body>
</html>

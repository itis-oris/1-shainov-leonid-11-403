<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Ошибка ${status}</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            text-align: center;
            padding: 50px;
            background-color: #f8f8f8;
            color: #333;
        }
        h1 { font-size: 6em; margin: 0; }
        h2 { font-size: 2em; margin: 10px 0; }
        p { font-size: 1.2em; }
        a { color: #007bff; text-decoration: none; }
        a:hover { text-decoration: underline; }
        .error-container {
            display: inline-block;
            padding: 40px;
            background: white;
            border-radius: 12px;
            box-shadow: 0 0 15px rgba(0,0,0,0.1);
        }
    </style>
</head>
<body>
<div class="error-container">
    <h1>${status}</h1>
    <h2>${message! "Произошла ошибка"}</h2>
    <p>К сожалению, что-то пошло не так.</p>
    <p><a href="${contextPath}/">Вернуться на главную</a></p>
</div>
</body>
</html>

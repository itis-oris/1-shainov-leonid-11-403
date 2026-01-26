<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Главная</title>
    <link rel="stylesheet" href="${contextPath}/static/css/home-page.css">
</head>
<body>

<div class="page-wrapper">
    <nav class="sidebar-nav">
        <form action="${contextPath}/home" method="post">
            <button type="submit">🏠</button>
        </form>
        <form action="${contextPath}/logout" method="post">
            <button type="submit">🚪</button>
        </form>
        <form action="${contextPath}/account" method="get">
            <button type="submit">👤</button>
        </form>
    </nav>

    <main class="content-wrapper">
        <section class="top-panel">
            <input type="text" id="searchInput" placeholder="Поиск треков">
            <select id="sortSelect">
                <option value="updated" selected>Сначала новые</option>
                <option value="alphabet">По алфавиту</option>
                <option value="created">По времени создания</option>
            </select>

            <div class="tags-filter">
                <select id="tagSelect">
                    <option value="">Выберите тег</option>
                    <#list allTags as tag>
                        <option value="${tag.id}" data-color="${tag.color!'#888'}">${tag.name}</option>
                    </#list>
                </select>
                <div id="selectedTags"></div>
            </div>
        </section>

        <section class="tracks-grid">
            <div class="track-card new-track">

            <div class="cover-container">
                    <div class="cover-placeholder">＋</div>
                </div>
                <div class="track-info">
                    <div class="track-title">Новый трек</div>
                    <div class="audio-placeholder small">Создайте новый трек!</div>
                </div>
            </div>

            <#list userTracks as track>
                <div class="track-card" data-track-id="${track.id}">
                    <div class="cover-container">
                        <#if track.imagePath?? && track.imagePath?has_content>
                            <img src="${contextPath}/files/${track.imagePath}" alt="${track.title!}" class="cover-image">
                        <#else>
                            <div class="cover-placeholder">Нет обложки</div>
                        </#if>
                        <button class="delete-track-btn">🗑</button>
                    </div>

                    <div class="track-info">
                        <div class="track-title">
                            <#if track.title?? && track.title?has_content>
                                ${track.title}
                            <#else>
                                Без названия
                            </#if>
                        </div>



                        <#if track.audioPath?? && track.audioPath?has_content>
                            <audio controls src="${contextPath}/files/${track.audioPath}"></audio>
                        <#else>
                            <div class="audio-placeholder small">Нет аудио</div>
                        </#if>
                    </div>
                </div>
            </#list>

        </section>
    </main>

</div>

<div id="status"></div>

<script>
    const contextPath = '${contextPath}';
</script>
<script src="${contextPath}/static/js/home-page.js"></script>

</body>
</html>

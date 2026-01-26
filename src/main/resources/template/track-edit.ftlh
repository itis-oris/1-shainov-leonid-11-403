<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>${track.title! "Редактирование трека"}</title>
    <link rel="stylesheet" href="${contextPath}/static/css/track-edit.css">
</head>
<body>

<div class="page-wrapper">
    <nav class="sidebar-nav">
        <button onclick="location.href='${contextPath}/home'">🏠</button>
        <form action="${contextPath}/logout" method="post">
            <button type="submit">🚪</button>
        </form>
        <form action="${contextPath}/account" method="get">
            <button type="submit">👤</button>
        </form>
    </nav>

    <main class="content-wrapper">
        <section class="left-panel">
            <div class="media-block">
                <div class="cover-section">
                    <div class="cover-container">
                        <#if track.imagePath?? && track.imagePath?has_content>
                            <img src="${contextPath}/files/${track.imagePath}"
                                 id="preview-image"
                                 class="cover-image">
                        <#else>
                            <div class="cover-placeholder">Нет обложки</div>
                        </#if>
                    </div>

                    <input type="file" name="image" id="imageInput" class="file-input" accept="image/*">
                    <label for="imageInput" class="file-button">Изменить обложку</label>
                </div>

                <div class="audio-section">
                    <#if track.audioPath?? && track.audioPath?has_content>
                        <audio controls id="preview-audio" class="audio-player"
                               src="${contextPath}/files/${track.audioPath}"></audio>
                    <#else>
                        <div class="audio-placeholder">Нет аудио</div>
                    </#if>

                    <input type="file" name="audio" id="audioInput" class="file-input" accept="audio/*">
                    <label for="audioInput" class="file-button">Добавить аудио</label>
                </div>

            </div>
        </section>

        <section class="center-panel">

            <form id="trackForm" enctype="multipart/form-data">
                <input type="hidden" name="id" value="${track.id!}">

                <div class="track-header">
                    <input type="text" name="title" class="track-title"
                           placeholder="Название трека"
                           value="${track.title}">
                    <button type="button" id="deleteTrackBtn" class="delete-track-btn">🗑️</button>
                </div>

                <label>Теги</label>
                <div id="tags-container">
                    <div class="tag add-tag">+</div>
                    <#if track.tags??>
                        <#list track.tags as tag>
                            <div class="tag" style="background-color:${tag.color! "#999"}"
                                 data-tag-id="${tag.id}">
                                ${tag.name}
                            </div>
                        </#list>
                    </#if>
                </div>

                <label for="description">Описание</label>
                <input id="description" name="description"
                       class="description-input"
                       value="${track.description!}">

                <label for="lyrics">Текст</label>
                <textarea id="lyrics" name="lyrics"
                          class="lyrics-input">${track.lyrics!}</textarea>

            </form>
        </section>

        <aside class="right-panel">
            <h3>Ваши треки</h3>

            <div class="tracks-list">
                <#list userTracks as t>
                    <div class="track-item"
                         onclick="location.href='${contextPath}/track?id=${t.id}'">
                        <div class="track-cover">
                            <#if t.imagePath?? && t.imagePath?has_content>
                                <img src="${contextPath}/files/${t.imagePath}" alt="">
                            <#else>
                                <div class="cover-placeholder small"></div>
                            </#if>
                        </div>
                        <div class="track-name">${t.title}</div>
                    </div>
                </#list>
            </div>
        </aside>

    </main>
</div>

<div id="status"></div>

<script>
    const contextPath = '${contextPath}';
    const trackId = '${track.id!}';
</script>
<script src="${contextPath}/static/js/track-edit.js"></script>
</body>
</html>

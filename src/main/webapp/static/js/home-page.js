const tracksGrid = document.querySelector('.tracks-grid');
const sortSelect = document.getElementById('sortSelect');
const searchInput = document.getElementById('searchInput');
const tagSelect = document.getElementById('tagSelect');
const selectedTagsContainer = document.getElementById('selectedTags');

let currentSort = sortSelect.value;
let currentSearch = '';
let selectedTags = [];

sortSelect.addEventListener('change', () => {
    currentSort = sortSelect.value;
    loadTracks();
});

searchInput.addEventListener('input', () => {
    currentSearch = searchInput.value.trim();
    loadTracks();
});

if (tagSelect) {
    tagSelect.addEventListener('change', () => {
        const selectedOption = tagSelect.selectedOptions[0];
        const tagObj = {
            id: selectedOption.value,
            name: selectedOption.text,
            color: selectedOption.dataset.color || '#777'
        };
        if (!selectedTags.find(t => t.id === tagObj.id)) {
            selectedTags.push(tagObj);
            renderSelectedTags();
            loadTracks();
        }
        tagSelect.value = '';
    });
}

function renderSelectedTags() {
    selectedTagsContainer.innerHTML = '';
    selectedTags.forEach(tagObj => {
        const pill = document.createElement('div');
        pill.className = 'tag-pill';
        pill.textContent = tagObj.name;
        pill.title = 'Нажмите, чтобы убрать';
        pill.style.backgroundColor = tagObj.color || '#777';
        pill.style.color = getContrastColor(tagObj.color || '#777');

        pill.addEventListener('click', () => {
            selectedTags = selectedTags.filter(t => t.id !== tagObj.id);
            renderSelectedTags();
            loadTracks();
        });
        selectedTagsContainer.appendChild(pill);
    });
}

function getContrastColor(hexColor) {
    if (!hexColor) return '#fff';
    hexColor = hexColor.replace('#', '');
    const r = parseInt(hexColor.substring(0,2),16);
    const g = parseInt(hexColor.substring(2,4),16);
    const b = parseInt(hexColor.substring(4,6),16);
    const brightness = (r*299 + g*587 + b*114)/1000;
    return brightness > 125 ? '#000' : '#fff';
}

async function loadTracks() {
    try {
        const params = new URLSearchParams();
        params.append('sort', currentSort);
        if (currentSearch) params.append('search', currentSearch);
        if (selectedTags.length) params.append('tags', selectedTags.map(t => t.name).join(','));

        const res = await fetch(`${contextPath}/tracks/list?${params.toString()}`);
        if (!res.ok) throw new Error('Ошибка сети');

        const data = await res.json();

        tracksGrid.querySelectorAll('.track-card:not(.new-track)').forEach(card => card.remove());

        data.tracks.forEach(track => {
            const card = document.createElement('div');
            card.className = 'track-card';
            card.dataset.trackId = track.id;

            card.innerHTML = `
                <div class="cover-container">
                    ${track.imagePath ? `<img src="${contextPath}/files/${track.imagePath}" class="cover-image">`
                : `<div class="cover-placeholder">Нет обложки</div>`}
                    <button class="delete-track-btn">🗑</button>
                </div>
                <div class="track-info">
                    <div class="track-title">${track.title || 'Без названия'}</div>
                    ${track.audioPath ? `<audio controls src="${contextPath}/files/${track.audioPath}"></audio>`
                : `<div class="audio-placeholder small">Нет аудио</div>`}
                </div>
            `;
            tracksGrid.appendChild(card);
        });
    } catch (e) {
        console.error(e);
        showStatus('Ошибка загрузки треков', false);
    }
}

tracksGrid?.addEventListener('click', async (evt) => {
    const card = evt.target.closest('.track-card');
    if (!card) return;

    if (evt.target.closest('.delete-track-btn')) {
        evt.stopPropagation();
        const trackId = card.dataset.trackId;
        if (!confirm("Удалить трек безвозвратно?")) return;

        try {
            const res = await fetch(`${contextPath}/track/delete`, {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: "trackId=" + encodeURIComponent(trackId)
            });
            const data = await res.json();
            if (data.success) {
                card.remove();
                showStatus("Трек удалён!");
            } else {
                showStatus("Ошибка: " + (data.error || ""), false);
            }
        } catch (e) {
            showStatus("Ошибка сети", false);
        }
        return;
    }

    if (!card.classList.contains('new-track')) {
        window.location.href = `${contextPath}/track?id=${card.dataset.trackId}`;
    }
});

document.querySelector('.track-card.new-track')?.addEventListener('click', () => {
    window.location.href = `${contextPath}/track/create`;
});

function showStatus(message, isSuccess = true) {
    const statusDiv = document.getElementById("status");
    statusDiv.textContent = message;
    statusDiv.className = isSuccess ? 'success' : 'error';
    statusDiv.style.display = 'block';
    setTimeout(() => statusDiv.style.display = 'none', 2000);
}

loadTracks();

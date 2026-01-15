const statusDiv = document.getElementById("status");
const trackForm = document.getElementById("trackForm");
const tagsContainer = document.getElementById("tags-container");
const imageInput = document.getElementById("imageInput");
const audioInput = document.getElementById("audioInput");


function showStatus(message, isSuccess = true) {
    if (!statusDiv) return;
    statusDiv.textContent = message;
    statusDiv.className = isSuccess ? 'success' : 'error';
    statusDiv.style.display = 'block';
    setTimeout(() => { statusDiv.style.display = 'none'; }, 2000);
}


async function postFormDataToEdit(formData) {
    try {
        const res = await fetch(contextPath + "/track/edit", { method: "POST", body: formData });
        const data = await res.json();
        return data;
    } catch (e) {
        console.error("postFormDataToEdit error", e);
        throw e;
    }
}

function createTagElement(tagData) {
    const tagDiv = document.createElement("div");
    tagDiv.className = "tag";
    tagDiv.textContent = tagData.name;
    tagDiv.style.backgroundColor = tagData.color || "#cccccc";
    if (tagData.id !== undefined) tagDiv.dataset.tagId = tagData.id;
    return tagDiv;
}

function hasTagWithName(name) {
    const existing = Array.from(tagsContainer.querySelectorAll(".tag"))
        .filter(el => !el.classList.contains("add-tag"))
        .some(t => t.textContent.trim().toLowerCase() === name.trim().toLowerCase());
    return existing;
}

if (trackForm) {
    trackForm.querySelectorAll("input[name], textarea").forEach(el => {
        if (el.type === "file") return;
        el.addEventListener("change", async () => {
            const formData = new FormData(trackForm);
            if (imageInput && imageInput.files && imageInput.files[0]) {
                formData.set("image", imageInput.files[0], imageInput.files[0].name);
            }
            if (audioInput && audioInput.files && audioInput.files[0]) {
                formData.set("audio", audioInput.files[0], audioInput.files[0].name);
            }
            try {
                const data = await postFormDataToEdit(formData);
                showStatus(data.success ? "Сохранено!" : "Ошибка: " + (data.error || ""), data.success);
            } catch (e) {
                showStatus("Ошибка сети", false);
            }
        });
    });
}

document.getElementById("deleteTrackBtn")?.addEventListener("click", async () => {
    if (!confirm("Удалить этот трек безвозвратно?")) return;

    try {
        const res = await fetch(contextPath + "/track/delete", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: "trackId=" + encodeURIComponent(trackId)
        });

        const data = await res.json();

        if (data.success) {
            showStatus("Трек удалён!", true);
            setTimeout(() => {
                location.href = contextPath + "/home";
            }, 800);
        } else {
            showStatus("Ошибка: " + (data.error || ""), false);
        }
    } catch (err) {
        console.error(err);
        showStatus("Ошибка сети", false);
    }
});


if (imageInput) {
    imageInput.addEventListener("change", async (e) => {
        const file = e.target.files[0];
        if (file) {
            // preview
            const img = document.getElementById("preview-image");
            if (img) {
                img.src = URL.createObjectURL(file);
            } else {
                const container = document.querySelector(".cover-container");
                if (container) {
                    const created = document.createElement("img");
                    created.id = "preview-image";
                    created.className = "cover-image";
                    created.src = URL.createObjectURL(file);
                    container.innerHTML = "";
                    container.appendChild(created);
                }
            }

            const formData = new FormData(trackForm);
            formData.set("image", file, file.name);
            if (audioInput && audioInput.files && audioInput.files[0]) {
                const a = audioInput.files[0];
                formData.set("audio", a, a.name);
            }
            try {
                const data = await postFormDataToEdit(formData);
                showStatus(data.success ? "Обложка загружена" : "Ошибка: " + (data.error || ""), data.success);
            } catch (e) {
                showStatus("Ошибка сети при загрузке обложки", false);
            }
        }
    });
}

if (audioInput) {
    audioInput.addEventListener("change", async (e) => {
        const file = e.target.files[0];
        if (file) {
            const audio = document.getElementById("preview-audio");
            if (audio) {
                audio.src = URL.createObjectURL(file);
            } else {
                const container = document.querySelector(".audio-section");
                if (container) {
                    const created = document.createElement("audio");
                    created.id = "preview-audio";
                    created.controls = true;
                    created.className = "audio-player";
                    created.src = URL.createObjectURL(file);
                    const placeholder = container.querySelector(".audio-placeholder");
                    if (placeholder) placeholder.replaceWith(created);
                    else container.appendChild(created);
                }
            }

            const formData = new FormData(trackForm);
            formData.set("audio", file, file.name);
            if (imageInput && imageInput.files && imageInput.files[0]) {
                const i = imageInput.files[0];
                formData.set("image", i, i.name);
            }
            try {
                const data = await postFormDataToEdit(formData);
                showStatus(data.success ? "Аудио загружено" : "Ошибка: " + (data.error || ""), data.success);
            } catch (e) {
                showStatus("Ошибка сети при загрузке аудио", false);
            }
        }
    });
}

if (tagsContainer) {
    tagsContainer.addEventListener("click", e => {
        if (e.target.classList.contains("add-tag")) {
            if (tagsContainer.querySelector(".new-tag-input")) return;

            const input = document.createElement("input");
            input.type = "text";
            input.placeholder = "Введите тег";
            input.className = "new-tag-input";

            const addBtn = tagsContainer.querySelector(".add-tag");
            if (addBtn && addBtn.nextSibling) {
                tagsContainer.insertBefore(input, addBtn.nextSibling);
            } else if (addBtn) {
                tagsContainer.appendChild(input);
            } else {
                tagsContainer.appendChild(input);
            }
            input.focus();

            let submitted = false;
            const submitTag = async () => {
                if (submitted) return;
                submitted = true;
                const tagName = input.value.trim();
                input.remove();
                if (!tagName) return;

                if (hasTagWithName(tagName)) {
                    showStatus("Такой тег уже есть", false);
                    return;
                }

                try {
                    const body = "trackId=" + encodeURIComponent(trackId) +
                        "&tagName=" + encodeURIComponent(tagName);
                    const res = await fetch(contextPath + "/track/tag/add", {
                        method: "POST",
                        headers: { "Content-Type": "application/x-www-form-urlencoded" },
                        body
                    });
                    const data = await res.json();
                    if (!data.success) {
                        showStatus("Ошибка: " + (data.error || "неизвестная"), false);
                        return;
                    }

                    const tagEl = createTagElement({ id: data.id, name: data.name, color: data.color });
                    const addBtn2 = tagsContainer.querySelector(".add-tag");
                    if (addBtn2 && addBtn2.nextSibling) tagsContainer.insertBefore(tagEl, addBtn2.nextSibling);
                    else tagsContainer.appendChild(tagEl);
                    showStatus("Тег добавлен");
                } catch (err) {
                    console.error("add tag error", err);
                    showStatus("Ошибка сети при добавлении тега", false);
                }
            };

            input.addEventListener("keydown", ev => {
                if (ev.key === "Enter") {
                    ev.preventDefault();
                    submitTag();
                }
            });
            input.addEventListener("blur", submitTag);
        }

        if (e.target.classList.contains("tag") && !e.target.classList.contains("add-tag")) {
            const tagEl = e.target;
            const tagId = tagEl.dataset.tagId;
            if (!tagId) return;

            fetch(contextPath + "/track/tag/remove", {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: "trackId=" + encodeURIComponent(trackId) + "&tagId=" + encodeURIComponent(tagId)
            })
                .then(r => r.json())
                .then(data => {
                    if (data.success) {
                        tagEl.remove();
                        showStatus("Тег удалён");
                    } else {
                        console.error("remove tag error response", data);
                        showStatus("Ошибка при удалении: " + (data.error || ""), false);
                    }
                })
                .catch(err => {
                    console.error("remove tag fetch error", err);
                    showStatus("Ошибка сети при удалении тега", false);
                });
        }
    });
}

package ru.deskofnotes.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class Track {
    private long id;
    private String title;
    private String description;
    private String lyrics;
    private String imagePath;
    private String audioPath;
    private long userId;
    private List<Tag> tags;
    private LocalDateTime updateDate;

    public Track() {
        tags = new ArrayList<>();
        updateDate = LocalDateTime.now();
    }

    public Track(long id, String title, String description, String lyrics, String imagePath, String audioPath, long userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.lyrics = lyrics;
        this.imagePath = imagePath;
        this.audioPath = audioPath;
        this.userId = userId;
        updateDate = LocalDateTime.now();
        tags = new ArrayList<>();
    }

}

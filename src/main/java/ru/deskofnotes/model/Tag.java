package ru.deskofnotes.model;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class Tag {
    private long id;
    private String name;
    private String color;
    private long userId;

    public Tag() {}
    public Tag(long id, String name, String color, long userId) {}
}

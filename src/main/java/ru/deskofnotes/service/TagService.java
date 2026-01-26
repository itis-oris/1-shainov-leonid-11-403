package ru.deskofnotes.service;

import ru.deskofnotes.model.Tag;
import ru.deskofnotes.repository.TagRepository;

import java.util.List;
import java.util.Random;

public class TagService {

    private TagRepository tagRepository;
    private final Random random = new Random();

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public Tag findOrCreate(String name, String color, long userId) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Тег не может быть пустым");
        }
        String normalized = name.trim().toLowerCase();
        if (normalized.length() > 20) {
            throw new IllegalArgumentException("Тег слишком длинный");
        }
        Tag existing = tagRepository.findByNameAndUserId(normalized, userId);
        if (existing != null) {
            return existing;
        }
        Tag tag = new Tag();
        tag.setName(normalized);
        tag.setColor(color != null && !color.isBlank() ? color : generateRandomColor());
        tag.setUserId(userId);
        return tagRepository.save(tag);
    }

    public List<Tag> getTagsForTrack(long trackId) {
        return tagRepository.findByTrackId(trackId);
    }

    public Tag addTagToTrack(long trackId, Tag tag) {
        if (tag == null || tag.getId() == 0) {
            throw new IllegalArgumentException("Тег пустой");
        }
        tagRepository.addTagToTrack(trackId, tag.getId());
        return tag;
    }

    public void removeTagFromTrack(long trackId, long tagId) {
        tagRepository.removeTagFromTrack(trackId, tagId);
    }

    public boolean isTagUsed(long tagId) {
        return tagRepository.isTagLinkedToAnyTrack(tagId);
    }

    public void deleteTag(long tagId) {
        tagRepository.delete(tagId);
    }

    public List<Tag> getAll() {
        return tagRepository.findAll();
    }

    private String generateRandomColor() {
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return String.format("#%02X%02X%02X", r, g, b);
    }
}

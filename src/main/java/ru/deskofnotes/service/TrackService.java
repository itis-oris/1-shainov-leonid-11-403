package ru.deskofnotes.service;

import jakarta.servlet.http.Part;
import ru.deskofnotes.model.Tag;
import ru.deskofnotes.model.Track;
import ru.deskofnotes.model.User;
import ru.deskofnotes.repository.TrackRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TrackService {

    private TrackRepository trackRepository;
    private TagService tagService;

    private static final String BASE_UPLOAD_PATH = System.getProperty("user.home")
            + File.separator + "deskofnotes"
            + File.separator + "uploads"
            + File.separator + "tracks";

    public TrackService(TrackRepository trackRepository, TagService tagService) {
        this.trackRepository = trackRepository;
        this.tagService = tagService;
    }

    public Track createTrack(User user) {
        Track track = new Track();
        track.setTitle("");
        track.setDescription("");
        track.setLyrics("");
        track.setAudioPath("");
        track.setImagePath("");
        track.setUserId(user.getId());
        track.setTags(new ArrayList<>());
        track.setUpdateDate(LocalDateTime.now());
        trackRepository.save(track);
        return track;
    }

    public Track findTrackById(long id) {
        return trackRepository.findById(id);
    }

    public void updateTrack(Track track) {
        if (track.getTitle().length() > 255) {
            throw new IllegalArgumentException("Название слишком длинное");
        }
        if (track.getDescription().length() > 255) {
            throw new IllegalArgumentException("Описание слишком длинное");
        }
        track.setUpdateDate(LocalDateTime.now());
        trackRepository.update(track);
    }

    public boolean deleteTrackById(long trackId, long userId) {
        Track track = findTrackById(trackId);
        if (track == null) return false;

        if (track.getTags() != null) {
            for (Tag tag : track.getTags()) {
                tagService.removeTagFromTrack(trackId, tag.getId());
                if (!tagService.isTagUsed(tag.getId())) {
                    tagService.deleteTag(tag.getId());
                }
            }
        }
        File trackDir = new File(BASE_UPLOAD_PATH + File.separator + trackId);
        deleteDirectoryRecursively(trackDir);

        return trackRepository.deleteTrackById(trackId, userId);
    }

    private void deleteDirectoryRecursively(File dir) {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        deleteDirectoryRecursively(f);
                    } else {
                        f.delete();
                    }
                }
            }
            dir.delete();
        }
    }

    public void addTagToTrack(long trackId, String tagName, String tagColor, long userId) {
        Tag tag = tagService.findOrCreate(tagName, tagColor, userId);
        tagService.addTagToTrack(trackId, tag);
    }

    public void removeTagFromTrack(long trackId, long tagId) {
        tagService.removeTagFromTrack(trackId, tagId);
        if (!tagService.isTagUsed(tagId)) {
            tagService.deleteTag(tagId);
        }
    }

    public Tag addTagToTrackReturnTag(long trackId, String tagName, String tagColor, long userId) {
        Tag tag = tagService.findOrCreate(tagName, tagColor, userId);
        tagService.addTagToTrack(trackId, tag);
        return tag;
    }

    public String saveFileAndGetPath(long trackId, Part filePart, String type) throws IOException {
        if (filePart == null || filePart.getSize() <= 0) return null;

        String mimeType = filePart.getContentType();
        if ((type.equals("audio") && !mimeType.startsWith("audio/")) ||
                (type.equals("image") && !mimeType.startsWith("image/"))) {
            throw new IOException("Недопустимый тип файла");
        }

        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

        File trackDir = new File(BASE_UPLOAD_PATH + File.separator + trackId);
        if (!trackDir.exists() && !trackDir.mkdirs()) {
            throw new IOException("Не удалось создать директорию для загрузки");
        }

        File[] existingFiles = trackDir.listFiles();
        if (existingFiles != null) {
            for (File f : existingFiles) {
                if (type.equals("audio") && f.getName().toLowerCase().endsWith(".mp3")) {
                    f.delete();
                } else if (type.equals("image") &&
                        (f.getName().toLowerCase().endsWith(".jpg")
                                || f.getName().toLowerCase().endsWith(".jpeg")
                                || f.getName().toLowerCase().endsWith(".png"))) {
                    f.delete();
                }
            }
        }

        String fullPath = trackDir.getAbsolutePath() + File.separator + fileName;
        filePart.write(fullPath);

        return "tracks/" + trackId + "/" + fileName;
    }

    public List<Track> getTracksByUser(long userId) {
        try {
            return trackRepository.getTracksByUser(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public List<Track> getTracksByUserFiltered(long userId, String sort, String search, List<String> tags) {
        List<Track> tracks = getTracksByUser(userId);

        for (Track t : tracks) {
            t.setTags(tagService.getTagsForTrack(t.getId()));
        }

        if (tags != null && !tags.isEmpty()) {
            List<String> normalizedTags = tags.stream()
                    .map(s -> s.trim().toLowerCase())
                    .toList();

            tracks.removeIf(t ->
                    t.getTags() == null ||
                            !t.getTags().stream()
                                    .map(tag -> tag.getName() == null ? "" : tag.getName().trim().toLowerCase())
                                    .collect(Collectors.toSet())
                                    .containsAll(normalizedTags)
            );
        }

        if (search != null && !search.isBlank()) {
            String s = search.toLowerCase();
            tracks.removeIf(t ->
                    t.getTitle() == null || !t.getTitle().toLowerCase().contains(s)
            );
        }

        switch (sort) {
            case "alphabet":
                tracks.sort(Comparator.comparing(
                        Track::getTitle,
                        Comparator.nullsFirst(String::compareToIgnoreCase)
                ));
                break;
            case "created":
                tracks.sort(Comparator.comparingLong(Track::getId));
                break;
            default:
                tracks.sort(Comparator.comparing(
                        Track::getUpdateDate,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ));
                break;
        }

        return tracks;
    }
}

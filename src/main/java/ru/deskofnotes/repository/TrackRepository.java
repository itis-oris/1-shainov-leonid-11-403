package ru.deskofnotes.repository;

import ru.deskofnotes.model.Tag;
import ru.deskofnotes.model.Track;
import ru.deskofnotes.service.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TrackRepository {
    private TagRepository tagRepository;

    public TrackRepository(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public void save(Track track) {
        try (Connection connection = DBConnection.getConnection()) {
            String SQL = "insert into tracks (title, description, lyrics, image_path, audio_path, user_id, update_date) values (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, track.getTitle());
                ps.setString(2, track.getDescription());
                ps.setString(3, track.getLyrics());
                ps.setString(4, track.getImagePath());
                ps.setString(5, track.getAudioPath());
                ps.setLong(6, track.getUserId());
                ps.setTimestamp(7, Timestamp.valueOf(track.getUpdateDate()));
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) track.setId(rs.getLong(1));
                }
            }
            if (track.getTags() != null && !track.getTags().isEmpty()) {
                for (Tag tag : track.getTags()) {
                    tagRepository.addTagToTrack(track.getId(), tag.getId());
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(Track track) {
        try (Connection connection = DBConnection.getConnection()) {
            String SQL = "update tracks set title = ?, description = ?, lyrics = ?, image_path = ?, audio_path = ?, user_id = ?, update_date = ? where id = ?";
            try (PreparedStatement ps = connection.prepareStatement(SQL)) {
                ps.setString(1, track.getTitle());
                ps.setString(2, track.getDescription());
                ps.setString(3, track.getLyrics());
                ps.setString(4, track.getImagePath());
                ps.setString(5, track.getAudioPath());
                ps.setLong(6, track.getUserId());
                ps.setTimestamp(7, Timestamp.valueOf(track.getUpdateDate()));
                ps.setLong(8, track.getId());
                ps.executeUpdate();
            }

            if (track.getTags() != null && !track.getTags().isEmpty()) {
                for (Tag tag : track.getTags()) {
                    tagRepository.addTagToTrack(track.getId(), tag.getId());
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Track findById(long id) {
        Track track = null;
        try (Connection connection = DBConnection.getConnection()) {
            String SQL = "select title, description, lyrics, image_path, audio_path, user_id, update_date from tracks where id = ?";
            try (PreparedStatement ps = connection.prepareStatement(SQL)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        track = new Track();
                        track.setId(id);
                        track.setTitle(rs.getString("title"));
                        track.setDescription(rs.getString("description"));
                        track.setLyrics(rs.getString("lyrics"));
                        track.setImagePath(rs.getString("image_path"));
                        track.setAudioPath(rs.getString("audio_path"));
                        track.setUserId(rs.getLong("user_id"));
                        track.setUpdateDate(rs.getTimestamp("update_date").toLocalDateTime());
                        track.setTags(tagRepository.findByTrackId(id));
                    }
                }
            }
            return track;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Track> getTracksByUser(long userId) throws SQLException {
        List<Track> tracks = new ArrayList<>();
        String sql = "SELECT id, title, image_path, audio_path, description, lyrics FROM tracks WHERE user_id = ? ORDER BY id DESC";
        try (Connection connection = DBConnection.getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setLong(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Track track = new Track();
                        track.setId(rs.getLong("id"));
                        track.setTitle(rs.getString("title"));
                        track.setImagePath(rs.getString("image_path"));
                        track.setAudioPath(rs.getString("audio_path"));
                        track.setDescription(rs.getString("description"));
                        track.setLyrics(rs.getString("lyrics"));
                        tracks.add(track);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return tracks;
    }

    public List<Track> getTracksByUserSorted(long userId, String sort) throws SQLException {
        List<Track> tracks = new ArrayList<>();
        String orderBy;
        switch (sort) {
            case "alphabet":
                orderBy = "title ASC";
                break;
            case "created":
                orderBy = "id DESC";
                break;
            default:
                orderBy = "update_date DESC";
                break;
        }

        String sql = "SELECT id, title, image_path, audio_path, description, lyrics, update_date " +
                "FROM tracks WHERE user_id = ? ORDER BY " + orderBy;

        try (Connection connection = DBConnection.getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setLong(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Track track = new Track();
                        track.setId(rs.getLong("id"));
                        track.setTitle(rs.getString("title"));
                        track.setImagePath(rs.getString("image_path"));
                        track.setAudioPath(rs.getString("audio_path"));
                        track.setDescription(rs.getString("description"));
                        track.setLyrics(rs.getString("lyrics"));
                        track.setUpdateDate(rs.getTimestamp("update_date").toLocalDateTime());
                        tracks.add(track);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return tracks;
    }

    public boolean deleteTrackById(long trackId, long userId) {
        String sql = """
        DELETE FROM tracks 
        WHERE id = ? AND user_id = ?
    """;

        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, trackId);
                ps.setLong(2, userId);
                int rows = ps.executeUpdate();
                try (PreparedStatement ps2 = conn.prepareStatement("DELETE FROM tracks_tags WHERE track_id = ?")) {
                    ps2.setLong(1, trackId);
                    ps2.executeUpdate();
                }

                return rows > 0;

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}

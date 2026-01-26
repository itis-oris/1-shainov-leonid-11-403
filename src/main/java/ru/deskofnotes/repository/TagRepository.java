package ru.deskofnotes.repository;

import ru.deskofnotes.model.Tag;
import ru.deskofnotes.service.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TagRepository {

    public Tag findByNameAndUserId(String name, long userId) {
        try (Connection connection = DBConnection.getConnection()) {
            String SQL = "select id, name, color, user_id from tags where lower(name) = lower(?) and user_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(SQL)) {
                ps.setString(1, name);
                ps.setLong(2, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Tag tag = new Tag();
                        tag.setId(rs.getLong("id"));
                        tag.setName(rs.getString("name"));
                        tag.setColor(rs.getString("color"));
                        tag.setUserId(rs.getLong("user_id"));
                        return tag;
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Tag save(Tag tag) {
        try (Connection connection = DBConnection.getConnection()) {
            String SQL = "insert into tags (name, color, user_id) values (?, ?, ?) returning id";
            try (PreparedStatement ps = connection.prepareStatement(SQL)) {
                ps.setString(1, tag.getName());
                ps.setString(2, tag.getColor());
                ps.setLong(3, tag.getUserId());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        tag.setId(rs.getLong("id"));
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return tag;
    }

    public List<Tag> findByTrackId(long trackId) {
        List<Tag> tags = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection()) {
            String SQL = """
                select t.id, t.name, t.color, t.user_id
                from tags t
                join tracks_tags tt on t.id = tt.tag_id
                where tt.track_id = ?
            """;
            try (PreparedStatement ps = connection.prepareStatement(SQL)) {
                ps.setLong(1, trackId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Tag tag = new Tag();
                        tag.setId(rs.getLong("id"));
                        tag.setName(rs.getString("name"));
                        tag.setColor(rs.getString("color"));
                        tag.setUserId(rs.getLong("user_id"));
                        tags.add(tag);
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return tags;
    }

    public void addTagToTrack(long trackId, long tagId) {
        try (Connection connection = DBConnection.getConnection()) {
            String SQL = "insert into tracks_tags (track_id, tag_id) values (?, ?) on conflict do nothing";
            try (PreparedStatement ps = connection.prepareStatement(SQL)) {
                ps.setLong(1, trackId);
                ps.setLong(2, tagId);
                ps.executeUpdate();
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeTagFromTrack(long trackId, long tagId) {
        try (Connection connection = DBConnection.getConnection()) {
            String SQL = "delete from tracks_tags where track_id = ? and tag_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(SQL)) {
                ps.setLong(1, trackId);
                ps.setLong(2, tagId);
                ps.executeUpdate();
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isTagLinkedToAnyTrack(long tagId) {
        String sql = "SELECT COUNT(*) FROM tracks_tags WHERE tag_id = ?";
        try (Connection connection = DBConnection.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setLong(1, tagId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public void delete(long tagId) {
        String sql = "DELETE FROM tags WHERE id = ?";
        try (Connection connection = DBConnection.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setLong(1, tagId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Tag> findAll() {
        List<Tag> tags = new ArrayList<>();
        String sql = "SELECT id, name, color, user_id FROM tags ORDER BY name ASC";

        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Tag tag = new Tag();
                        tag.setId(rs.getLong("id"));
                        tag.setName(rs.getString("name"));
                        tag.setColor(rs.getString("color"));
                        tag.setUserId(rs.getLong("user_id"));
                        tags.add(tag);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return tags;
    }
}

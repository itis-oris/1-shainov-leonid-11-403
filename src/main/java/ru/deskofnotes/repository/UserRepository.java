package ru.deskofnotes.repository;

import ru.deskofnotes.model.User;
import ru.deskofnotes.service.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {
    public void save(User user){
        try (Connection connection = DBConnection.getConnection()) {
            String SQLid = "select nextval('user_seq') as id";
            connection.setAutoCommit(false);
            Long id = null;
            try (PreparedStatement preparedStatement = connection.prepareStatement(SQLid)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        id = resultSet.getLong(1);
                    }
                }
            }

            if (id != null) {
                user.setId(id);
            } else {
                throw new RuntimeException();
            }

            String SQLusers = "insert into users (id, username, hashpassword) values (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(SQLusers)) {
                preparedStatement.setLong(1, id);
                preparedStatement.setString(2, user.getUsername());
                preparedStatement.setString(3, user.getHashPassword());
                preparedStatement.executeUpdate();
            }
            connection.commit();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public String getPasswordHash(String username) {
        String result = null;
        try (Connection connection = DBConnection.getConnection()) {
            String SQL = "select hashpassword from users where username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
                preparedStatement.setString(1, username);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        result = resultSet.getString("hashpassword");
                    }
                }
            }

            return result;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public User getUser(String username) {
        User result = null;
        try (Connection connection = DBConnection.getConnection()) {
            String SQL = "select id, username, hashpassword from users where username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
                preparedStatement.setString(1, username);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        result = new User();
                        result.setId(resultSet.getLong("id"));
                        result.setHashPassword(resultSet.getString("hashpassword"));
                        result.setUsername(resultSet.getString("username"));
                    }
                }
            }
            return result;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteById(Long userId) {
        try (Connection connection = DBConnection.getConnection()) {
            String SQL = "DELETE FROM users WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
                preparedStatement.setLong(1, userId);
                int affected = preparedStatement.executeUpdate();
                if (affected == 0) {
                    throw new RuntimeException("Пользователь не найден");
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}

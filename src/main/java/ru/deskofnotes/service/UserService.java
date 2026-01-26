package ru.deskofnotes.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.deskofnotes.model.User;
import ru.deskofnotes.repository.UserRepository;

public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addUser(User user) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setHashPassword(passwordEncoder.encode(user.getHashPassword()));
        if (userRepository.getUser(user.getUsername()) == null) {
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("Пользователь с таким именем уже существует");
        }
    }

    public User checkUser(String username, String password) {
        String hashPassword = userRepository.getPasswordHash(username);
        if (hashPassword == null) {
            return null;
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(password, hashPassword)) {
            return null;
        }

        return userRepository.getUser(username);
    }

    public User getUser(String username) {
        return userRepository.getUser(username);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}

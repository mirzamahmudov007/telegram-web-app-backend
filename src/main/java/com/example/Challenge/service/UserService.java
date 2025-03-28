package com.example.Challenge.service;

import com.example.Challenge.model.User;
import com.example.Challenge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByTelegramId(String telegramId) {
        return userRepository.findByTelegramId(telegramId);
    }

    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByTelegramId(String telegramId) {
        return userRepository.existsByTelegramId(telegramId);
    }

    @Transactional
    public User registerUser(String telegramId, String username, String firstName, String lastName) {
        if (existsByTelegramId(telegramId)) {
            return getUserByTelegramId(telegramId).orElseThrow();
        }

        User user = new User();
        user.setTelegramId(telegramId);
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole("USER");

        return saveUser(user);
    }
    @Transactional
    public User changeUserRole(Long userId, String role) {
        User user = getUserById(userId).get();
        user.setRole(role);
        return userRepository.save(user);
    }
    @Transactional
    public User makeAdmin(Long userId) {
        User user = getUserById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole("ADMIN");
        return saveUser(user);
    }

    @Transactional
    public User removeAdmin(Long userId) {
        User user = getUserById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole("USER");
        return saveUser(user);
    }

    @Transactional
    public void addPermissionToUser(Long userId, String permissionId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.addPermission(permissionId);
            userRepository.save(user);
        });
    }

    @Transactional
    public void removePermissionFromUser(Long userId, String permissionId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.removePermission(permissionId);
            userRepository.save(user);
        });
    }

}


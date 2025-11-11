package com.michael.ebeano.services;

import com.michael.ebeano.models.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserServiceImpl implements UserService {

    private final Map<UUID, User> users = new ConcurrentHashMap<>();

    // CRUD basics
    public Optional<User> getById(UUID id) {
        return Optional.ofNullable(users.get(id));
    }

    public List<User> listAll() {
        return new ArrayList<>(users.values());
    }

    public void delete(UUID id) {
        users.remove(id);
    }

    public List<User> findByField(String fieldName, Object value) {
        return null;
    }

    // Domain-specific
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(u -> email != null && email.equals(u.email()))
                .findFirst();
    }

    public User register(String email, String passwordHash, String firstName, String lastName, String phone) {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        User user = new User(id, email, passwordHash, firstName, lastName, phone, false, now, now);
        users.put(id, user);
        return user;
    }

    public Optional<User> verifyEmail(UUID userId) {
        User u = users.get(userId);
        if (u == null) return Optional.empty();
        User updated = new User(u.id(), u.email(), u.passwordHash(), u.firstName(), u.lastName(), u.phone(), true, u.createdAt(), LocalDateTime.now());
        users.put(userId, updated);
        return Optional.of(updated);
    }

    public Optional<User> updateNames(UUID userId, String firstName, String lastName) {
        User u = users.get(userId);
        if (u == null) return Optional.empty();
        User updated = new User(u.id(), u.email(), u.passwordHash(), firstName, lastName, u.phone(), u.emailVerified(), u.createdAt(), LocalDateTime.now());
        users.put(userId, updated);
        return Optional.of(updated);
    }
}

package com.michael.ebeano.services;

import com.michael.ebeano.models.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public interface UserService {

    Optional<User> getById(UUID id);

    List<User> listAll();

    void delete(UUID id);

    List<User> findByField(String fieldName, Object value);

    // Domain-specific
    Optional<User> findByEmail(String email);

    User register(String email, String passwordHash, String firstName, String lastName, String phone);

    Optional<User> verifyEmail(UUID userId);

    Optional<User> updateNames(UUID userId, String firstName, String lastName);
}

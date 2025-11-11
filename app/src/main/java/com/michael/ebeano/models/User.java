package com.michael.ebeano.models;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Record representing USERS table.
 */
public record User(
        UUID id,
        String email,
        String passwordHash,
        String firstName,
        String lastName,
        String phone,
        boolean emailVerified,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
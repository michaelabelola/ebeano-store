package com.michael.ebeano.models;

import java.time.LocalDateTime;

/**
 * Record representing CATEGORIES table.
 */
public record Category(
        int id,
        String name,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
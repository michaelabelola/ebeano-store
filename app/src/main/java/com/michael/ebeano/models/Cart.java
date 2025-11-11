package com.michael.ebeano.models;

import java.time.LocalDateTime;

/**
 * Record representing CARTS table.
 */
public record Cart(
        int id,
        int userId,
        boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
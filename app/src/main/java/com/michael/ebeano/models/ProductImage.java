package com.michael.ebeano.models;

import java.time.LocalDateTime;

/**
 * Record representing PRODUCT_IMAGES table.
 */
public record ProductImage(
        int id,
        int productId,
        String url,
        boolean isPrimary,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
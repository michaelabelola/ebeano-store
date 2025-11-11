package com.michael.ebeano.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Record representing PRODUCTS table.
 */
public record Product(
        int id,
        String sku,
        String name,
        String shortDescription,
        String longDescription,
        BigDecimal price,
        int stockQuantity,
        boolean isActive,
        int categoryId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
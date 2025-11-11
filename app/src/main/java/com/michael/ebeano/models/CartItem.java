package com.michael.ebeano.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Record representing CART_ITEMS table.
 */
public record CartItem(
        int id,
        int cartId,
        int productId,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal discountPrice,
        LocalDateTime addedAt
) {
}
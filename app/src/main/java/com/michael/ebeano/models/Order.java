package com.michael.ebeano.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Record representing ORDERS table.
 */
public record Order(
        int id,
        String orderNumber,
        int userId,
        int shippingAddressId,
        int billingAddressId,
        BigDecimal subtotal,
        BigDecimal tax,
        BigDecimal shippingCost,
        BigDecimal total,
        OrderStatus status,
        LocalDateTime placedAt,
        LocalDateTime updatedAt
) {
}
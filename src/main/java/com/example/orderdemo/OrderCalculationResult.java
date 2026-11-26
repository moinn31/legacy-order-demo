package com.example.orderdemo;

import java.math.BigDecimal;

/**
 * Immutable result of an order total calculation.
 */
public final class OrderCalculationResult {

    private final BigDecimal unitPrice;
    private final int quantity;
    private final BigDecimal discountPercent;
    private final BigDecimal subtotal;
    private final BigDecimal discountAmount;
    private final BigDecimal total;

    public OrderCalculationResult(
            BigDecimal unitPrice,
            int quantity,
            BigDecimal discountPercent,
            BigDecimal subtotal,
            BigDecimal discountAmount,
            BigDecimal total) {
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.discountPercent = discountPercent;
        this.subtotal = subtotal;
        this.discountAmount = discountAmount;
        this.total = total;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public BigDecimal getTotal() {
        return total;
    }
}

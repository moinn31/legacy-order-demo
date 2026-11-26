package com.example.orderdemo;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Calculates order subtotals, discounts, and final totals using currency rounding.
 */
public final class OrderCalculator {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    private static final int CURRENCY_SCALE = 2;

    /**
     * Calculates the order total for the given price, quantity, and discount percentage.
     *
     * @param unitPrice       non-negative unit price
     * @param quantity        non-negative item quantity
     * @param discountPercent discount percentage in the range [0, 100]
     * @return calculated order breakdown
     * @throws IllegalArgumentException if any input is null or out of valid range
     */
    public OrderCalculationResult calculate(BigDecimal unitPrice, int quantity, BigDecimal discountPercent) {
        if (unitPrice == null) {
            throw new IllegalArgumentException("Product price is required.");
        }
        if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Product price cannot be negative.");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        if (discountPercent == null) {
            throw new IllegalArgumentException("Discount percentage is required.");
        }
        if (discountPercent.compareTo(BigDecimal.ZERO) < 0 || discountPercent.compareTo(ONE_HUNDRED) > 0) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100.");
        }

        BigDecimal normalizedPrice = unitPrice.setScale(CURRENCY_SCALE, RoundingMode.HALF_UP);
        BigDecimal normalizedDiscount = discountPercent.setScale(CURRENCY_SCALE, RoundingMode.HALF_UP);

        BigDecimal subtotal = unitPrice
                .multiply(BigDecimal.valueOf(quantity))
                .setScale(CURRENCY_SCALE, RoundingMode.HALF_UP);

        BigDecimal discountAmount = subtotal
                .multiply(discountPercent)
                .divide(ONE_HUNDRED, CURRENCY_SCALE, RoundingMode.HALF_UP);

        BigDecimal total = subtotal
                .subtract(discountAmount)
                .setScale(CURRENCY_SCALE, RoundingMode.HALF_UP);

        return new OrderCalculationResult(
                normalizedPrice,
                quantity,
                normalizedDiscount,
                subtotal,
                discountAmount,
                total
        );
    }
}

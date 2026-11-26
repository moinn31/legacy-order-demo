package com.example.orderdemo;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class OrderCalculatorTest {

    private final OrderCalculator calculator = new OrderCalculator();

    @Test
    public void calculatesTotalWithoutDiscount() {
        OrderCalculationResult result = calculator.calculate(
                new BigDecimal("19.99"),
                3,
                BigDecimal.ZERO
        );

        assertEquals(new BigDecimal("59.97"), result.getSubtotal());
        assertEquals(new BigDecimal("0.00"), result.getDiscountAmount());
        assertEquals(new BigDecimal("59.97"), result.getTotal());
    }

    @Test
    public void calculatesTotalWithDiscount() {
        OrderCalculationResult result = calculator.calculate(
                new BigDecimal("25.00"),
                4,
                new BigDecimal("15")
        );

        assertEquals(new BigDecimal("100.00"), result.getSubtotal());
        assertEquals(new BigDecimal("15.00"), result.getDiscountAmount());
        assertEquals(new BigDecimal("85.00"), result.getTotal());
    }

    @Test
    public void calculatesFullDiscount() {
        OrderCalculationResult result = calculator.calculate(
                new BigDecimal("49.95"),
                2,
                new BigDecimal("100")
        );

        assertEquals(new BigDecimal("99.90"), result.getSubtotal());
        assertEquals(new BigDecimal("99.90"), result.getDiscountAmount());
        assertEquals(new BigDecimal("0.00"), result.getTotal());
    }

    @Test
    public void roundsHalfUpToTwoDecimalPlaces() {
        OrderCalculationResult result = calculator.calculate(
                new BigDecimal("19.99"),
                1,
                new BigDecimal("33.33")
        );

        // 19.99 * 33.33 / 100 = 6.662667 -> 6.66
        assertEquals(new BigDecimal("19.99"), result.getSubtotal());
        assertEquals(new BigDecimal("6.66"), result.getDiscountAmount());
        assertEquals(new BigDecimal("13.33"), result.getTotal());

        OrderCalculationResult halfUpBoundary = calculator.calculate(
                new BigDecimal("10.005"),
                1,
                BigDecimal.ZERO
        );
        assertEquals(new BigDecimal("10.01"), halfUpBoundary.getTotal());
    }

    @Test
    public void handlesZeroQuantity() {
        OrderCalculationResult result = calculator.calculate(
                new BigDecimal("29.99"),
                0,
                new BigDecimal("20")
        );

        assertEquals(0, result.getQuantity());
        assertEquals(new BigDecimal("0.00"), result.getSubtotal());
        assertEquals(new BigDecimal("0.00"), result.getDiscountAmount());
        assertEquals(new BigDecimal("0.00"), result.getTotal());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNullPrice() {
        calculator.calculate(null, 1, BigDecimal.ZERO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNegativePrice() {
        calculator.calculate(new BigDecimal("-0.01"), 1, BigDecimal.ZERO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNegativeQuantity() {
        calculator.calculate(new BigDecimal("10.00"), -1, BigDecimal.ZERO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNullDiscount() {
        calculator.calculate(new BigDecimal("10.00"), 1, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNegativeDiscount() {
        calculator.calculate(new BigDecimal("10.00"), 1, new BigDecimal("-1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsDiscountOverOneHundred() {
        calculator.calculate(new BigDecimal("10.00"), 1, new BigDecimal("100.01"));
    }
}

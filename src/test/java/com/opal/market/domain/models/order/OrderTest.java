package com.opal.market.domain.models.order;

import com.opal.market.domain.models.instruments.Equity;
import com.opal.market.domain.models.PriceSpecification;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    @Test
    public void testRemainingQuantity1() {
        Order sellOrder = new Order(OrderSide.SELL, new Equity("LRCX"), new BigDecimal("100.5"), 80, 1L);
        Order buyOrder = new Order(OrderSide.BUY, new Equity("LRCX"), new BigDecimal("120"), 10, 2L);
        PriceSpecification priceSpecification = new PriceSpecification();
        priceSpecification.setBuyPrice(buyOrder.getPrice());

        try {
            sellOrder.trade(buyOrder, priceSpecification);

            assertEquals(10, sellOrder.getExecutedQuantity());
        } catch (OrderInvalidStatusException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRemainingQuantity2() {
        Order sellOrder = new Order(OrderSide.SELL, new Equity("LRCX"), new BigDecimal("100.5"), 80, 1L);
        Order buyOrder = new Order(OrderSide.BUY, new Equity("LRCX"), new BigDecimal("100"), 10, 2L);
        PriceSpecification priceSpecification = new PriceSpecification();
        priceSpecification.setBuyPrice(buyOrder.getPrice());

        try {
            assertNull(sellOrder.trade(buyOrder, priceSpecification));
        } catch (OrderInvalidStatusException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSamePrice1() {
        List<Order> orders = new ArrayList<>();

        orders.add(new Order(OrderSide.SELL, new Equity("LRCX"), new BigDecimal("100.5"), 80, 1L));
        orders.add(new Order(OrderSide.SELL, new Equity("LRCX"), new BigDecimal("100.5"), 20, 2L));
        orders.add(new Order(OrderSide.SELL, new Equity("LRCX"), new BigDecimal("100.5"), 10, 3L));

        Order buyOrder = new Order(OrderSide.BUY, new Equity("LRCX"), new BigDecimal("100.5"), 120, 4L);

        PriceSpecification priceSpecification = new PriceSpecification();
        priceSpecification.setBuyPrice(buyOrder.getPrice());

        try {
            for (Order sellOrder : orders) {
                sellOrder.trade(buyOrder, priceSpecification);
            }

            assertEquals(80, orders.get(0).getExecutedQuantity());
            assertEquals(20, orders.get(1).getExecutedQuantity());
            assertEquals(10, orders.get(2).getExecutedQuantity());
        } catch (OrderInvalidStatusException e) {
            e.printStackTrace();
        }
    }
}

package com.opal.market.domain.models.order;

import com.opal.market.domain.models.instruments.Equity;
import com.opal.market.domain.models.SORT_DIR;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderComparatorTest {

    @Test
    public void testSort() {
        List<Order> orders = new ArrayList<>();

        orders.add(new Order(OrderSide.SELL, new Equity("LRCX"), BigDecimal.valueOf(100), 5, 4L));
        orders.add(new Order(OrderSide.SELL, new Equity("LRCX"), BigDecimal.valueOf(99), 6, 2L));
        orders.add(new Order(OrderSide.SELL, new Equity("LRCX"), BigDecimal.valueOf(101), 100, 5L));
        orders.add(new Order(OrderSide.SELL, new Equity("LRCX"), BigDecimal.valueOf(98), 100, 1L));
        orders.add(new Order(OrderSide.SELL, new Equity("LRCX"), BigDecimal.valueOf(99), 100, 3L));

        List<Order> expected = new ArrayList<>();
        expected.add(orders.get(3));
        expected.add(orders.get(1));
        expected.add(orders.get(4));
        expected.add(orders.get(0));
        expected.add(orders.get(2));

        orders.sort(new OrderComparator(SORT_DIR.ASC));

        assertEquals(orders, expected);

        for(Order order : orders) {
            System.out.println(order);
        }
    }
}

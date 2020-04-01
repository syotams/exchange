package com.opal.market.domain.models.market;

import com.opal.market.domain.models.equity.Equity;
import com.opal.market.domain.models.order.Order;
import com.opal.market.domain.models.order.OrderSide;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class OrderBookTest {

    @Test
    public void testSellBook() {
        OrderBook orderBook = new OrderBook();
        orderBook.addOrder(new Order(OrderSide.SELL, new Equity("TSEM"), BigDecimal.valueOf(100), 10, 1L));
        orderBook.addOrder(new Order(OrderSide.SELL, new Equity("TSEM"), BigDecimal.valueOf(100), 20, 2L));
        orderBook.addOrder(new Order(OrderSide.SELL, new Equity("TSEM"), BigDecimal.valueOf(100), 15, 3L));

        assertEquals(15, orderBook.getSellBook().get(2).getQuantity());
    }

    @Test
    public void testBuyBook() {
        OrderBook orderBook = new OrderBook();
        orderBook.addOrder(new Order(OrderSide.BUY, new Equity("TSEM"), BigDecimal.valueOf(100), 10, 1L));
        orderBook.addOrder(new Order(OrderSide.BUY, new Equity("TSEM"), BigDecimal.valueOf(100), 20, 2L));
        orderBook.addOrder(new Order(OrderSide.BUY, new Equity("TSEM"), BigDecimal.valueOf(100), 15, 3L));

        assertEquals(15, orderBook.getBuyBook().get(2).getQuantity());
    }

}

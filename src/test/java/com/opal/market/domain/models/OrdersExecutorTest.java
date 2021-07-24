package com.opal.market.domain.models;

import com.opal.market.domain.models.equity.Equity;
import com.opal.market.domain.models.market.OrderBook;
import com.opal.market.domain.models.order.Order;
import com.opal.market.domain.models.order.OrderSide;
import com.opal.market.domain.models.market.OrdersExecutor;
import com.opal.market.domain.service.order.OrdersService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrdersExecutorTest {

    @Test
    public void testExecution() {
        OrderBook orderBook = new OrderBook();
        
        Order sellOrder1 = new Order(OrderSide.SELL, new Equity("LRCX"), new BigDecimal("100.5"), 80, 1L);
        Order sellOrder2 = new Order(OrderSide.SELL, new Equity("LRCX"), new BigDecimal("100.7"), 100, 2L);
        Order sellOrder3 = new Order(OrderSide.SELL, new Equity("LRCX"), new BigDecimal("100.3"), 15, 3L);

        orderBook.addOrder(sellOrder1);
        orderBook.addOrder(sellOrder2);
        orderBook.addOrder(sellOrder3);

        Order buyOrder1 = new Order(OrderSide.BUY, new Equity("LRCX"), new BigDecimal("99.5"), 80, 5L);
        Order buyOrder2 = new Order(OrderSide.BUY, new Equity("LRCX"), new BigDecimal("100.7"), 50, 4L);
        Order buyOrder3 = new Order(OrderSide.BUY, new Equity("LRCX"), new BigDecimal("101.3"), 15, 6L);

        orderBook.addOrder(buyOrder1);
        orderBook.addOrder(buyOrder2);
        orderBook.addOrder(buyOrder3);

        OrdersExecutor ordersExecutor = new OrdersExecutor(new OrdersService());
        ordersExecutor.setOrderBook(orderBook);
        ordersExecutor.call();

        assertEquals(1, orderBook.getBuyBook().size());
    }
    
    @Test
    public void testExecuteOrder() {
        OrderBook orderBook = new OrderBook();

        Order sellOrder1 = new Order(OrderSide.SELL, new Equity("LRCX"), new BigDecimal("100"), 10, 1L);
        Order sellOrder2 = new Order(OrderSide.SELL, new Equity("LRCX"), new BigDecimal("102"), 20, 2L);
        Order sellOrder3 = new Order(OrderSide.SELL, new Equity("LRCX"), new BigDecimal("98"), 15, 3L);

        orderBook.addOrder(sellOrder1);
        orderBook.addOrder(sellOrder2);
        orderBook.addOrder(sellOrder3);

        Order buyOrder1 = new Order(OrderSide.BUY, new Equity("LRCX"), new BigDecimal("104"), 10, 5L);
        Order buyOrder2 = new Order(OrderSide.BUY, new Equity("LRCX"), new BigDecimal("102"), 20, 4L);
        Order buyOrder3 = new Order(OrderSide.BUY, new Equity("LRCX"), new BigDecimal("106"), 15, 6L);

        orderBook.addOrder(buyOrder1);
        orderBook.addOrder(buyOrder2);
        orderBook.addOrder(buyOrder3);

        OrdersExecutor ordersExecutor = new OrdersExecutor(new OrdersService());
        ordersExecutor.setOrderBook(orderBook);
        ordersExecutor.call();

        orderBook.print();
        assertEquals(0, orderBook.getBuyBook().size());
    }

}

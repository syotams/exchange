package com.opal.market.domain.models;

import com.opal.market.application.market.Market;
import com.opal.market.domain.models.market.OneByOneMarketStrategy;
import com.opal.market.domain.models.market.OrderBook;
import com.opal.market.domain.models.equity.Equity;
import com.opal.market.domain.models.order.Order;
import com.opal.market.domain.models.order.OrderSide;
import com.opal.market.domain.service.order.OrdersExecutor;
import com.opal.market.domain.service.order.OrdersService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrdersExecutorTest {

    @Test
    public void testExecution() throws InterruptedException {
        MarketService market = new Market(new OrdersExecutor(new OrdersService()), new OneByOneMarketStrategy());

        Order sellOrder1 = new Order(OrderSide.SELL, new Equity("LRCX"), new BigDecimal("100.5"), 80, 1L);
        Order sellOrder2 = new Order(OrderSide.SELL, new Equity("LRCX"), new BigDecimal("100.7"), 100, 2L);
        Order sellOrder3 = new Order(OrderSide.SELL, new Equity("LRCX"), new BigDecimal("100.3"), 15, 3L);

        market.immediatelyAddOrder(sellOrder1);
        market.immediatelyAddOrder(sellOrder2);
        market.immediatelyAddOrder(sellOrder3);

        Order buyOrder1 = new Order(OrderSide.BUY, new Equity("LRCX"), new BigDecimal("99.5"), 80, 5L);
        Order buyOrder2 = new Order(OrderSide.BUY, new Equity("LRCX"), new BigDecimal("100.7"), 50, 4L);
        Order buyOrder3 = new Order(OrderSide.BUY, new Equity("LRCX"), new BigDecimal("101.3"), 15, 6L);

        market.immediatelyAddOrder(buyOrder1);
        market.immediatelyAddOrder(buyOrder2);
        market.immediatelyAddOrder(buyOrder3);

        Map<String, OrderBook> books = market.getBooks();
        OrdersExecutor ordersExecutor = new OrdersExecutor(new OrdersService());
        ordersExecutor.execute(books);

        assertEquals(1, books.get("LRCX").getBuyBook().size());
    }
    
    @Test
    public void testExecuteOrder() throws InterruptedException {
        Market market = new Market(new OrdersExecutor(new OrdersService()), new OneByOneMarketStrategy());

        Order sellOrder1 = new Order(OrderSide.SELL, new Equity("LRCX"), new BigDecimal("100"), 10, 1L);
        Order sellOrder2 = new Order(OrderSide.SELL, new Equity("LRCX"), new BigDecimal("102"), 20, 2L);
        Order sellOrder3 = new Order(OrderSide.SELL, new Equity("LRCX"), new BigDecimal("98"), 15, 3L);

        market.immediatelyAddOrder(sellOrder1);
        market.immediatelyAddOrder(sellOrder2);
        market.immediatelyAddOrder(sellOrder3);

        Order buyOrder1 = new Order(OrderSide.BUY, new Equity("LRCX"), new BigDecimal("104"), 10, 5L);
        Order buyOrder2 = new Order(OrderSide.BUY, new Equity("LRCX"), new BigDecimal("102"), 20, 4L);
        Order buyOrder3 = new Order(OrderSide.BUY, new Equity("LRCX"), new BigDecimal("106"), 15, 6L);

        market.immediatelyAddOrder(buyOrder1);
        market.immediatelyAddOrder(buyOrder2);
        market.immediatelyAddOrder(buyOrder3);

        Map<String, OrderBook> books = market.getBooks();
        OrdersExecutor ordersExecutor = new OrdersExecutor(new OrdersService());
        ordersExecutor.execute(books);

        market.print();
        assertEquals(0, books.get("LRCX").getBuyBook().size());
    }

}
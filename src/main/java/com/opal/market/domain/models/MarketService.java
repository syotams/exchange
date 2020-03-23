package com.opal.market.domain.models;

import com.opal.market.domain.models.market.OrderBook;
import com.opal.market.domain.models.order.Order;

import java.util.List;
import java.util.Map;

public interface MarketService {

    void addOrder(Order order) throws InterruptedException;

    OrderBook getOrderBook(String symbol);

    void execute();

    void start();

    void setRunning(boolean isRunning);

    void join(long i) throws InterruptedException;

    void immediatelyAddOrder(Order order);

    Map<String, OrderBook> getBooks();

    void addOrders(List<Order> orders) throws InterruptedException;

    int getTotalReceived();
}

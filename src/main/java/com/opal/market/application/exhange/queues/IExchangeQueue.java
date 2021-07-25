package com.opal.market.application.exhange.queues;

import com.opal.market.application.exhange.IQueue;
import com.opal.market.application.exhange.NonBlockingTask;
import com.opal.market.domain.models.order.Order;
import com.opal.market.domain.models.order.OrderSide;

import java.util.Map;

public interface IExchangeQueue<T> extends IQueue<T> {
    void setRunning(boolean isRunning);

    boolean isRunning();

    int getTotalHandled();

    int getTotalReceived();

    NonBlockingTask<Order[]> getOrderBook(String symbol, OrderSide side);

    NonBlockingTask<Map<String, String>> stats();
}

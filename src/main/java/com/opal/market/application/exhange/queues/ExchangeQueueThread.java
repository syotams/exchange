package com.opal.market.application.exhange.queues;

import com.opal.market.application.exhange.NonBlockingTask;
import com.opal.market.domain.models.market.Exchange;
import com.opal.market.domain.models.order.Order;
import com.opal.market.domain.models.order.OrderSide;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ExchangeQueueThread extends AbstractQueueThread<Order> implements IExchangeQueue<Order> {
    protected final Exchange exchange;

    protected final BlockingQueue<NonBlockingTask<?>> tasks = new ArrayBlockingQueue<>(100);

    public ExchangeQueueThread(Exchange exchange, String className) {
        super(className);
        this.exchange = exchange;
    }

    @Override
    public NonBlockingTask<Order[]> getOrderBook(String symbol, OrderSide side) {
        NonBlockingTask<Order[]> task = new NonBlockingTask<>(() -> {
            switch(side) {
                case SELL:
                    return exchange.getSellBook(symbol);
                default:
                case BUY:
                    return exchange.getBuyBook(symbol);
            }
        });
        tasks.add(task);
        return task;
    }

    public NonBlockingTask<Map<String, String>> stats() {
        NonBlockingTask<Map<String, String>> task = new NonBlockingTask<>(exchange::stats);
        tasks.add(task);
        return task;
    }

    // For performance test
    public NonBlockingTask<Map<String, Boolean>> hasMatch() {
        NonBlockingTask<Map<String, Boolean>> task = new NonBlockingTask<>(exchange::hasMatch);
        tasks.add(task);
        return task;
    }
}

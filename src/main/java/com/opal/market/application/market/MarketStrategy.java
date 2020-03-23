package com.opal.market.application.market;

import com.opal.market.domain.models.MarketService;
import com.opal.market.domain.models.order.Order;

import java.util.concurrent.BlockingQueue;

public interface MarketStrategy extends Runnable {

    void init(MarketService marketService, BlockingQueue<Order> ordersQueue);

    void setRunning(boolean isRunning);

    boolean isRunning();

    void join(long time) throws InterruptedException;

    int getTotalOrdersHandled();
}

package com.opal.market.domain.models.market;

import com.opal.market.application.market.MarketStrategy;
import com.opal.market.domain.models.MarketService;
import com.opal.market.domain.models.order.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
public class OneByOneMarketStrategy extends Thread implements MarketStrategy {

    private MarketService market;

    private BlockingQueue<Order> ordersQueue;

    private boolean isRunning;

    private int totalOrdersHandled;


    public void init(MarketService marketService, BlockingQueue<Order> ordersQueue) {
        this.market = marketService;
        this.ordersQueue = ordersQueue;
        start();
    }

    @Override
    public void run() {
        isRunning = true;

        Order order;

        try {
            int i=0;

            while (isRunning) {
                if (null != (order = ordersQueue.poll(5, TimeUnit.MILLISECONDS))) {
                    market.getOrderBook(order.getEquity().getSymbol()).addOrder(order);
                    totalOrdersHandled++;

                    if((i++ % 50) == 0) {
                        market.execute();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("------------- Market closed and served " + totalOrdersHandled +" orders -----------");
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    @Override
    public int getTotalOrdersHandled() {
        return totalOrdersHandled;
    }
}

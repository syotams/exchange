package com.opal.market.domain.models.market;

import com.opal.market.application.market.MarketStrategy;
import com.opal.market.domain.models.MarketService;
import com.opal.market.domain.models.order.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

@Component
public class IntervalMarketStrategy extends Thread implements MarketStrategy {

    private MarketService market;

    private BlockingQueue<Order> ordersQueue;

    private boolean isRunning;

    private int totalOrdersHandled;


    @Override
    public void run() {
        List<Order> orders = new ArrayList<>();

        isRunning = true;
        int size = 0;

        try {
            while (isRunning) {
                ordersQueue.drainTo(orders);

                if((size = orders.size()) > 0) {
                    market.addOrders(orders);
                    totalOrdersHandled += size;
                    orders.clear();
                }

                market.execute();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("------------- Market closed and served " + totalOrdersHandled + " orders -----------");
    }

    @Override
    public void init(MarketService marketService, BlockingQueue<Order> ordersQueue) {
        this.market = marketService;
        this.ordersQueue = ordersQueue;
        start();
    }

    @Override
    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    @Override
    public boolean isRunning() {
        return this.isRunning;
    }

    public int getTotalOrdersHandled() {
        return totalOrdersHandled;
    }
}

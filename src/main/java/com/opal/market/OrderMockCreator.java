package com.opal.market;

import com.opal.market.application.market.IntervalMarketQueueThread;
import com.opal.market.domain.models.equity.Equity;
import com.opal.market.domain.models.order.Order;
import com.opal.market.domain.models.order.OrderSide;

import java.math.BigDecimal;
import java.util.concurrent.Callable;

public class OrderMockCreator implements Callable<Long> {

    private IntervalMarketQueueThread marketQueue;

    public OrderMockCreator(IntervalMarketQueueThread marketQueue) {
        this.marketQueue = marketQueue;
    }

    @Override
    public Long call() {
        long startTime = System.currentTimeMillis();

        try {
            int i = 16667;
            int timeInterval = 10;

            do {
                Order sellOrder1 = new Order(OrderSide.SELL, new Equity("LRCX"), new BigDecimal(String.valueOf( ((int) (Math.random()*2)+50) )), (int) (Math.random()*100+1), 1L);
                Order buyOrder1  = new Order(OrderSide.BUY, new Equity("LRCX"), new BigDecimal(String.valueOf( ((int) (Math.random()*2)+50) )), (int) (Math.random()*100+1), 2L);
                Order sellOrder2 = new Order(OrderSide.SELL, new Equity("BA"), new BigDecimal(String.valueOf( ((int) (Math.random()*2)+70) )), (int) (Math.random()*100+1), 1L);
                Order buyOrder2  = new Order(OrderSide.BUY, new Equity("BA"), new BigDecimal(String.valueOf( ((int) (Math.random()*2)+70) )), (int) (Math.random()*100+1), 2L);
                Order sellOrder3 = new Order(OrderSide.SELL, new Equity("MU"), new BigDecimal(String.valueOf( ((int) (Math.random()*2)+150) )), (int) (Math.random()*100+1), 1L);
                Order buyOrder3  = new Order(OrderSide.BUY, new Equity("MU"), new BigDecimal(String.valueOf( ((int) (Math.random()*2)+150) )), (int) (Math.random()*100+1), 2L);

                marketQueue.addItem(sellOrder1);
//                Thread.sleep(timeInterval);

                marketQueue.addItem(buyOrder1);
//                Thread.sleep(timeInterval);

                marketQueue.addItem(sellOrder2);
//                Thread.sleep(timeInterval);

                marketQueue.addItem(buyOrder2);
//                Thread.sleep(timeInterval);

                marketQueue.addItem(sellOrder3);
//                Thread.sleep(timeInterval);

                marketQueue.addItem(buyOrder3);
//                Thread.sleep(timeInterval);
            }
            while (i-- >= 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return System.currentTimeMillis() - startTime;
    }
}

package com.opal.market;

import com.opal.market.domain.models.MarketService;
import com.opal.market.domain.models.equity.Equity;
import com.opal.market.domain.models.order.Order;
import com.opal.market.domain.models.order.OrderSide;

import java.math.BigDecimal;
import java.util.concurrent.Callable;

public class OrderMockCreator implements Callable<Long> {

    private MarketService market;

    public OrderMockCreator(MarketService market) {
        this.market = market;
    }

    @Override
    public Long call() {
        long startTime = System.currentTimeMillis();

        try {
            int i = 100;
            int timeInterval = 10;

            do {
                Order sellOrder1 = new Order(OrderSide.SELL, new Equity("LRCX"), new BigDecimal(String.valueOf( ((int) (Math.random()*3)+50) )), (int) (Math.random()*100), 1L);
                Order buyOrder1  = new Order(OrderSide.BUY, new Equity("LRCX"), new BigDecimal(String.valueOf( ((int) (Math.random()*3)+50) )), (int) (Math.random()*100), 2L);
//                Order sellOrder2 = new Order(OrderSide.SELL, new Equity("BA"), new BigDecimal(String.valueOf( ((int) (Math.random()*3)+70) )), (int) (Math.random()*100), 1L);
//                Order buyOrder2  = new Order(OrderSide.BUY, new Equity("BA"), new BigDecimal(String.valueOf( ((int) (Math.random()*3)+70) )), (int) (Math.random()*100), 2L);
//                Order sellOrder3 = new Order(OrderSide.SELL, new Equity("MU"), new BigDecimal(String.valueOf( ((int) (Math.random()*5)+150) )), (int) (Math.random()*100), 1L);
//                Order buyOrder3  = new Order(OrderSide.BUY, new Equity("MU"), new BigDecimal(String.valueOf( ((int) (Math.random()*5)+150) )), (int) (Math.random()*100), 2L);

                market.addOrder(sellOrder1);
                Thread.sleep(timeInterval);

                market.addOrder(buyOrder1);
                Thread.sleep(timeInterval);

//                market.addOrder(sellOrder2);
//                Thread.sleep(timeInterval);
//
//                market.addOrder(buyOrder2);
//                Thread.sleep(timeInterval);
//
//                market.addOrder(sellOrder3);
//                Thread.sleep(timeInterval);
//
//                market.addOrder(buyOrder3);
//                Thread.sleep(timeInterval);
            }
            while (i-- > 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return System.currentTimeMillis() - startTime;
    }
}

package com.opal.market;

import com.opal.market.application.market.IntervalMarketQueueThread;
import com.opal.market.domain.models.equity.Equity;
import com.opal.market.domain.models.order.Order;
import com.opal.market.domain.models.order.OrderSide;

import java.math.BigDecimal;
import java.math.MathContext;
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
            int i = 1000;//16667;
            int timeInterval = 10;
            MathContext mathContext = new MathContext(5);

            BigDecimal bigDecimal50 = new BigDecimal((Math.random() * 2) + 50, mathContext);
            BigDecimal bigDecimal70 = new BigDecimal((Math.random() * 2) + 70, mathContext);
            BigDecimal bigDecimal150 = new BigDecimal((Math.random() * 2) + 150, mathContext);

            Order[] orders = new Order[12];

            do {
                orders[0] = new Order(OrderSide.SELL, new Equity("LRCX"), bigDecimal50, (int) (Math.random()*100+1), 1L);
                orders[1]  = new Order(OrderSide.BUY, new Equity("LRCX"), bigDecimal50, (int) (Math.random()*100+1), 2L);
                orders[2] = new Order(OrderSide.SELL, new Equity("BA"), bigDecimal70, (int) (Math.random()*100+1), 1L);
                orders[3]  = new Order(OrderSide.BUY, new Equity("BA"), bigDecimal70, (int) (Math.random()*100+1), 2L);
                orders[4] = new Order(OrderSide.SELL, new Equity("MU"), bigDecimal150, (int) (Math.random()*100+1), 1L);
                orders[5]  = new Order(OrderSide.BUY, new Equity("MU"), bigDecimal150, (int) (Math.random()*100+1), 2L);
                orders[6] = new Order(OrderSide.SELL, new Equity("GE"), bigDecimal50, (int) (Math.random()*100+1), 1L);
                orders[7]  = new Order(OrderSide.BUY, new Equity("GE"), bigDecimal50, (int) (Math.random()*100+1), 2L);
                orders[8] = new Order(OrderSide.SELL, new Equity("TSEM"), bigDecimal70, (int) (Math.random()*100+1), 1L);
                orders[9]  = new Order(OrderSide.BUY, new Equity("TSEM"), bigDecimal70, (int) (Math.random()*100+1), 2L);
                orders[10] = new Order(OrderSide.SELL, new Equity("AIG"), bigDecimal150, (int) (Math.random()*100+1), 1L);
                orders[11]  = new Order(OrderSide.BUY, new Equity("AIG"), bigDecimal150, (int) (Math.random()*100+1), 2L);

                for (Order order: orders) {
                    marketQueue.addItem(order);
//                    Thread.sleep(timeInterval);
                }
            }
            while (i-- >= 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return System.currentTimeMillis() - startTime;
    }
}

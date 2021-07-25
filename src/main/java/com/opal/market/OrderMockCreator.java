package com.opal.market;

import com.opal.market.application.exhange.ExchangeApplicationService;
import com.opal.market.application.exhange.IExchangeApplicationService;
import com.opal.market.application.exhange.queues.IntervalExchangeQueueThread;
import com.opal.market.application.exhange.OrdersApplicationService;
import com.opal.market.domain.models.instruments.Equity;
import com.opal.market.domain.models.order.Order;
import com.opal.market.domain.models.order.OrderSide;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.concurrent.Callable;

public class OrderMockCreator implements Callable<Long> {

    private final IntervalExchangeQueueThread marketQueue;
    private final OrdersApplicationService ordersApplicationService;

    private String[] instruments;

    private final int numberOfIter;

    private final int numberPerIter;


    public OrderMockCreator(IntervalExchangeQueueThread marketQueue, String[] instruments, int numberOfIter, int numberPerIter) {
        IExchangeApplicationService exchangeService = new ExchangeApplicationService(marketQueue);
        this.ordersApplicationService = new OrdersApplicationService(exchangeService);
        this.marketQueue = marketQueue;
        this.instruments = instruments;
        this.numberOfIter = numberOfIter;
        this.numberPerIter = numberPerIter;
    }

    @Override
    public Long call() {
        long startTime = System.currentTimeMillis();

        try {
            int i = numberOfIter;
            int timeInterval = 10;
            MathContext mathContext = new MathContext(5);

            BigDecimal bigDecimal50 = new BigDecimal((Math.random() * 2) + 50, mathContext);
            Order[] orders = new Order[numberPerIter];

            do {
                for(int j=0; j<orders.length; j+=2) {
                    String instrument = instruments[(int) (Math.random()*instruments.length)];
                    orders[j] = new Order(OrderSide.BUY, new Equity(instrument), bigDecimal50, (int) (Math.random() * 500 + 1), (long) (Math.random()*20));
                    orders[j+1] = new Order(OrderSide.SELL, new Equity(instrument), bigDecimal50, (int) (Math.random() * 500 + 1), (long) (Math.random()*20));
                }

                ordersApplicationService.addItems(orders);
                //Thread.sleep(timeInterval);
            }
            while (i-- >= 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return System.currentTimeMillis() - startTime;
    }
}

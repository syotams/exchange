package com.opal.market.application.exhange.queues;

import com.opal.market.domain.models.market.Exchange;
import com.opal.market.domain.models.order.Order;
import org.springframework.stereotype.Service;

@Service
public class UnitExchangeQueueThread extends ExchangeQueueThread {

    public UnitExchangeQueueThread(Exchange exchange) {
        super(exchange, Class.class.getSimpleName());
    }

    @Override
    public void run() {
        setRunning(true);
        Order order;

        try {
            while (isRunning) {
                // TODO: redundant class, queue could be shared with exchange
                if (null != (order = queue.poll())) {
                    exchange.addOrder(order);
                    totalHandled++;
                    exchange.execute();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("------------- Market closed and served " + totalHandled +" orders -----------");
    }
}

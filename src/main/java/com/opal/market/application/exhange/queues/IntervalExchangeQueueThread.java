package com.opal.market.application.exhange.queues;

import com.opal.market.domain.models.market.Exchange;
import com.opal.market.domain.models.order.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class IntervalExchangeQueueThread extends ExchangeQueueThread {

    public IntervalExchangeQueueThread(Exchange exchange) {
        super(exchange, Class.class.getSimpleName());
    }

    @Override
    public void run() {
        List<Order> orders = new ArrayList<>();
        setRunning(true);
        int size;

        try {
            while (isRunning) {
                queue.drainTo(orders);

                if((size = orders.size()) > 0) {
                    exchange.addOrders(orders);
                    totalHandled += size;
                    orders.clear();
                }

                exchange.execute();

                if(tasks.size() > 0) {
                    tasks.poll().run();
                }

                Thread.sleep(100);
            }

            exchange.shutDown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.format("------------- Market closed and received %d and handled %d orders -----------%n",
                totalReceived,
                totalHandled);
        System.out.println(exchange.stats());
    }
}

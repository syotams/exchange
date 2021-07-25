package com.opal.market.application.exhange;

import com.opal.market.application.exhange.queues.IExchangeQueue;
import com.opal.market.domain.models.order.Order;
import com.opal.market.domain.models.order.OrderSide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeoutException;

@Service
public class ExchangeApplicationService implements IExchangeApplicationService {

    private final static Logger log = LoggerFactory.getLogger(ExchangeApplicationService.class);

    private final IExchangeQueue<Order> exchangeQueue;


    @Autowired
    public ExchangeApplicationService(@Qualifier("intervalExchangeQueueThread") IExchangeQueue<Order> exchangeQueue) {
        this.exchangeQueue = exchangeQueue;
    }

    @Override
    public void addOrder(Order order) throws InterruptedException {
        exchangeQueue.addItem(order);
    }

    @Override
    public Order[] getOrderBook(String symbol, OrderSide side) {
        NonBlockingTask<Order[]> book = exchangeQueue.getOrderBook(symbol, side);

        try {
            return book.get(1000);
        } catch (InterruptedException | TimeoutException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public Map<String, String> stats() {
        Map<String, String> stats = exchangeQueue.stats().get();
        stats.put("totalReceived", String.valueOf(exchangeQueue.getTotalReceived()));
        stats.put("totalHandled", String.valueOf(exchangeQueue.getTotalHandled()));
        return stats;
    }
}

package com.opal.market.application.market;

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
public class MarketApplicationService implements IMarketApplicationService {

    private Logger log = LoggerFactory.getLogger(MarketApplicationService.class);

    private IMarketQueue marketQueue;


    @Autowired
    public MarketApplicationService(@Qualifier("intervalMarketQueueThread") IMarketQueue marketQueue) {
        this.marketQueue = marketQueue;
    }

    @Override
    public void addOrder(Order order) throws InterruptedException {
        marketQueue.addItem(order);
    }

    @Override
    public Order[] getOrderBook(String symbol, OrderSide side) {
        NonBlockingTask<Order[]> book = marketQueue.getOrderBook(symbol, side);

        try {
            return book.get(1000);
        } catch (InterruptedException | TimeoutException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public Map<String, String> stats() {
        Map<String, String> stats = marketQueue.stats().get();
        stats.put("totalReceived", String.valueOf(marketQueue.getTotalReceived()));
        stats.put("totalHandled", String.valueOf(marketQueue.getTotalHandled()));
        return stats;
    }
}

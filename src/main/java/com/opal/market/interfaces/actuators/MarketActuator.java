package com.opal.market.interfaces.actuators;

import com.opal.market.application.market.IMarketApplicationService;
import com.opal.market.domain.models.order.Order;
import com.opal.market.domain.models.order.OrderSide;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Endpoint(id = "market")
public class MarketActuator {

    private IMarketApplicationService marketApplicationService;


    public MarketActuator(@Autowired IMarketApplicationService marketApplicationService) {
        this.marketApplicationService = marketApplicationService;
    }

    @ReadOperation
    public Map<String, String> stats() {
        return marketApplicationService.stats();
    }

    @ReadOperation
    public OrderBookResponse orderBook(@Selector String symbol) {
        Order[] buyBook = marketApplicationService.getOrderBook(symbol, OrderSide.BUY);
        Order[] sellBook = marketApplicationService.getOrderBook(symbol, OrderSide.SELL);

        return new OrderBookResponse(buyBook, sellBook);
    }

}

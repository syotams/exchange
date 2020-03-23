package com.opal.market.interfaces.api;

import com.opal.market.domain.models.MarketService;
import com.opal.market.domain.models.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1.0/equities")
public class EquityResource {

    private final MarketService marketService;


    public EquityResource(@Autowired MarketService marketService) {
        this.marketService = marketService;
    }

    @GetMapping("/{symbol}/book")
    public OrdersResponse all(@PathVariable(value = "symbol") String symbol) {
        List<Order> buyOrders = marketService.getOrderBook(symbol).getBuyBook();
        List<Order> sellOrders = marketService.getOrderBook(symbol).getSellBook();

        return new OrdersResponse(buyOrders, sellOrders);
    }

}

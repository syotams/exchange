package com.opal.market.interfaces.api;

import com.opal.market.application.exhange.IExchangeApplicationService;
import com.opal.market.domain.models.order.Order;
import com.opal.market.domain.models.order.OrderSide;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1.0/equities")
public class EquityResource {

    private final IExchangeApplicationService marketApplicationService;

    @Autowired
    public EquityResource(IExchangeApplicationService marketApplicationService) {
        this.marketApplicationService = marketApplicationService;
    }

    @GetMapping("/{symbol}/book")
    public OrdersResponse all(@PathVariable(value = "symbol") String symbol) {
        Order[] buyOrders = marketApplicationService.getOrderBook(symbol, OrderSide.BUY);
        Order[] sellOrders = marketApplicationService.getOrderBook(symbol, OrderSide.SELL);

        return new OrdersResponse(buyOrders, sellOrders);
    }

}

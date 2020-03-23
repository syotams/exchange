package com.opal.market.application.market;

import com.opal.market.domain.models.MarketService;
import com.opal.market.domain.models.equity.Equity;
import com.opal.market.domain.models.order.Order;
import com.opal.market.domain.models.order.OrderSide;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrdersApplicationService {

    private MarketService marketService;


    public OrdersApplicationService(@Autowired MarketService marketService) {
        this.marketService = marketService;
    }

    public Order createOrder(Long userId, String symbol, OrderSide side, BigDecimal price, int quantity) throws InterruptedException {
        Equity equity = new Equity(symbol);
        Order order = new Order(side, equity, price, quantity, userId);
        marketService.addOrder(order);
        return order;
    }
}

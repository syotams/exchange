package com.opal.market.application.market;

import com.opal.market.domain.models.equity.Equity;
import com.opal.market.domain.models.order.Order;
import com.opal.market.domain.models.order.OrderSide;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrdersApplicationService {

    private IMarketApplicationService marketApplicationService;


    @Autowired
    public OrdersApplicationService(IMarketApplicationService marketApplicationService) {
        this.marketApplicationService = marketApplicationService;
    }

    public Order createOrder(Long userId, String symbol, OrderSide side, BigDecimal price, int quantity) throws InterruptedException {
        Equity equity = new Equity(symbol);
        Order order = new Order(side, equity, price, quantity, userId);
        marketApplicationService.addOrder(order);
        return order;
    }
}

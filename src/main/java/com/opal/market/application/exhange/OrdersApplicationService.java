package com.opal.market.application.exhange;

import com.opal.market.domain.models.instruments.Equity;
import com.opal.market.domain.models.order.Order;
import com.opal.market.domain.models.order.OrderSide;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrdersApplicationService {

    private IExchangeApplicationService exchangeApplicationService;


    @Autowired
    public OrdersApplicationService(IExchangeApplicationService exchangeApplicationService) {
        this.exchangeApplicationService = exchangeApplicationService;
    }

    public Order createOrder(Long userId, String symbol, OrderSide side, BigDecimal price, int quantity) throws InterruptedException {
        Equity equity = new Equity(symbol);
        Order order = new Order(side, equity, price, quantity, userId);
        exchangeApplicationService.addOrder(order);
        return order;
    }

    public void addItems(Order[] orders) throws InterruptedException {
        for (Order order : orders) {
            exchangeApplicationService.addOrder(order);
        }
    }
}

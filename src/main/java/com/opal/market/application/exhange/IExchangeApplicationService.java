package com.opal.market.application.exhange;

import com.opal.market.domain.models.order.Order;
import com.opal.market.domain.models.order.OrderSide;

import java.util.Map;

public interface IExchangeApplicationService {

    void addOrder(Order order) throws InterruptedException;

    Order[] getOrderBook(String symbol, OrderSide side);

    Map<String, String> stats();
}

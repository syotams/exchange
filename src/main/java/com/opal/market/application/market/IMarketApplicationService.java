package com.opal.market.application.market;

import com.opal.market.domain.models.order.Order;
import com.opal.market.domain.models.order.OrderSide;

import java.util.Map;

public interface IMarketApplicationService {

    void addOrder(Order order) throws InterruptedException;

    Order[] getOrderBook(String symbol, OrderSide side);

    Map<String, String> stats();
}

package com.opal.market.interfaces.api;

import com.opal.market.domain.models.order.Order;

import java.util.List;

public class OrdersResponse {

    private List<Order> buy;

    private List<Order> sell;

    public OrdersResponse(List<Order> buyOrders, List<Order> sellOrders) {
        buy = buyOrders;
        sell = sellOrders;
    }

    public List<Order> getBuy() {
        return buy;
    }

    public void setBuy(List<Order> buy) {
        this.buy = buy;
    }

    public List<Order> getSell() {
        return sell;
    }

    public void setSell(List<Order> sell) {
        this.sell = sell;
    }
}

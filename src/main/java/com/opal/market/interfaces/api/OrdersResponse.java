package com.opal.market.interfaces.api;

import com.opal.market.domain.models.order.Order;

public class OrdersResponse {

    private Order[] buy;

    private Order[] sell;

    public OrdersResponse(Order[] buyOrders, Order[] sellOrders) {
        buy = buyOrders;
        sell = sellOrders;
    }

    public Order[] getBuy() {
        return buy;
    }

    public void setBuy(Order[] buy) {
        this.buy = buy;
    }

    public Order[] getSell() {
        return sell;
    }

    public void setSell(Order[] sell) {
        this.sell = sell;
    }
}

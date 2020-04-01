package com.opal.market.interfaces.actuators;

import com.opal.market.domain.models.order.Order;

public class OrderBookResponse {

    private Order[] buy;

    private Order[] sell;


    public OrderBookResponse(Order[] buy, Order[] sell) {
        this.buy = buy;
        this.sell = sell;
    }

    public Order[] getBuy() {
        return buy;
    }

    public Order[] getSell() {
        return sell;
    }
}

package com.opal.market.domain.models.order;

public class OrdersDoesNotMatchException extends Throwable {
    public OrdersDoesNotMatchException(String message) {
        super(message);
    }
}

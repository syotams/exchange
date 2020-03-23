package com.opal.market.domain.models.order;

public class OrderInvalidStatusException extends Exception {
    public OrderInvalidStatusException() {
    }

    public OrderInvalidStatusException(String message) {
        super(message);
    }
}

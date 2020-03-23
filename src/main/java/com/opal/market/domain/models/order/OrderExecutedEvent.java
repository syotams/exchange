package com.opal.market.domain.models.order;

import org.springframework.context.ApplicationEvent;

public class OrderExecutedEvent extends ApplicationEvent {
    public OrderExecutedEvent(Object source) {
        super(source);
    }
}

package com.opal.market.domain.service.order;

import com.opal.market.domain.models.PriceSpecification;
import com.opal.market.domain.models.order.Execution;
import com.opal.market.domain.models.order.Order;
import com.opal.market.domain.models.order.OrderInvalidStatusException;
import com.opal.market.domain.models.order.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrdersService {

    private final static Logger log = LoggerFactory.getLogger(OrdersService.class);


    private List<Order> match(Order buyOrder, List<Order> sellOrders, PriceSpecification priceSpecification) {
        List<Order> matched = new ArrayList<>();
        int sellIndex = 0;
        int accumulatedQuantity = 0;

        priceSpecification.setBuyPrice(buyOrder.getPrice());

        while((accumulatedQuantity < (buyOrder.getRemainingQuantity() - accumulatedQuantity)) && sellIndex < sellOrders.size()) {
            Order sellOrder = sellOrders.get(sellIndex);

            if (priceSpecification.isSatisfiedBy(sellOrder.getPrice())) {
                matched.add(sellOrder);
            }

            accumulatedQuantity += Math.min(buyOrder.getQuantity() - accumulatedQuantity, sellOrder.getRemainingQuantity());
            sellIndex++;
        }

        return matched;
    }

    private List<Order> execute(Order buyOrder, List<Order> sellOrders, PriceSpecification priceSpecification) throws OrderInvalidStatusException {
        List<Order> executedOrders = new ArrayList<>();

        for (Order sellOrder : sellOrders) {
            Execution execution = sellOrder.trade(buyOrder, priceSpecification);

            log.info("OrderExecuted:" + sellOrder);

            // match all sell orders before execution
            if (sellOrder.getStatus() == OrderStatus.EXECUTED) {
                executedOrders.add(sellOrder);
            }
        }

        return executedOrders;
    }

    public boolean hasMatch(List<Order> buyOrders, List<Order> sellOrders, PriceSpecification priceSpecification) {
        if(buyOrders.size() == 0 || sellOrders.size() == 0) {
            return false;
        }

        priceSpecification.setBuyPrice(buyOrders.get(0).getPrice());
        return priceSpecification.isSatisfiedBy(sellOrders.get(0).getPrice());
    }
}

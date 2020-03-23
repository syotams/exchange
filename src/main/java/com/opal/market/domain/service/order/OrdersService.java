package com.opal.market.domain.service.order;

import com.opal.market.domain.models.PriceSpecification;
import com.opal.market.domain.models.order.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrdersService {

    private Logger log = LoggerFactory.getLogger(OrdersService.class);


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

    private List<Order> execute(Order buyOrder, List<Order> sellOrders, PriceSpecification priceSpecification) throws OrdersDoesNotMatchException, OrderInvalidStatusException {
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

    public List<Order> matchAndExecute(Order buyOrder, List<Order> sellOrders, PriceSpecification priceSpecification) {
        List<Order> executedOrders = new ArrayList<>();
        int sellIndex = 0;
        int accumulatedQuantity = 0;
        Execution execution;

        priceSpecification.setBuyPrice(buyOrder.getPrice());

        try {
            while ((accumulatedQuantity < buyOrder.getRemainingQuantity()) && (sellIndex < sellOrders.size())) {
                Order sellOrder = sellOrders.get(sellIndex);

                execution = sellOrder.trade(buyOrder, priceSpecification);

                if (sellOrder.getStatus() == OrderStatus.EXECUTED) {
                    executedOrders.add(sellOrder);
                    log.info("OrderExecuted:" + sellOrder);
//                    log.info(sellOrder.getExecutions());
                }
                accumulatedQuantity += execution.getQuantity();
                sellIndex++;
            }
        }
        catch (OrdersDoesNotMatchException e) {}
        catch (OrderInvalidStatusException e) {
//            sellOrders.remove(sellIndex);
//            matchAndExecute(buyOrder, sellOrders);
        }

        return executedOrders;
    }
}

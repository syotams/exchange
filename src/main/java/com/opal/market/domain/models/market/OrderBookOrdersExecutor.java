package com.opal.market.domain.models.market;

import com.opal.market.domain.models.PriceSpecification;
import com.opal.market.domain.models.order.Execution;
import com.opal.market.domain.models.order.Order;
import com.opal.market.domain.models.order.OrderInvalidStatusException;
import com.opal.market.domain.models.order.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Callable;

/**
 * TODO: should OrdersService and OrderBookOrdersExecutor should be merged for better performance?
 */
public class OrderBookOrdersExecutor implements Callable<Integer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderBookOrdersExecutor.class);

    private OrderBook orderBook;

    private final PriceSpecification priceSpecification;


    public OrderBookOrdersExecutor() {
        priceSpecification = new PriceSpecification();
    }

    public void setOrderBook(OrderBook orderBook) {
        this.orderBook = orderBook;
    }

    @Override
    public Integer call() {
        List<Order> buyOrders  = orderBook.getBuyBook();
        List<Order> sellOrders = orderBook.getSellBook();

        ListIterator<Order> buyOrdersIterator = buyOrders.listIterator();

        int totalExecuted = 0;

        while (buyOrdersIterator.hasNext()) {
            Order buyOrder = buyOrdersIterator.next();
            List<Order> executedOrders = matchAndExecute(buyOrder, sellOrders, priceSpecification);

            // remove buy order if executed all quantity
            if(buyOrder.getStatus() == OrderStatus.EXECUTED) {
                buyOrdersIterator.remove();
                totalExecuted++;
            }

            if(executedOrders.size()>0) {
                sellOrders.removeAll(executedOrders);
                totalExecuted += executedOrders.size();
                LOGGER.info("OrderExecuted:" + buyOrder);
            }
            else if(buyOrder.getStatus() != OrderStatus.EXECUTED) {
                break;
            }
        }

        return totalExecuted;
    }

    private List<Order> matchAndExecute(Order buyOrder, List<Order> sellOrders, PriceSpecification priceSpecification) {
        List<Order> executedOrders = new ArrayList<>();
        int sellIndex = 0;

        priceSpecification.setBuyPrice(buyOrder.getPrice());

        try {
            while ((0 < buyOrder.getRemainingQuantity()) && (sellIndex < sellOrders.size())) {
                Order sellOrder = sellOrders.get(sellIndex);

                Execution execution = sellOrder.trade(buyOrder, priceSpecification);

                if(null==execution) {
                    break;
                }

                if (sellOrder.getStatus() == OrderStatus.EXECUTED) {
                    executedOrders.add(sellOrder);
                    LOGGER.info("OrderExecuted:" + sellOrder);
                }
                sellIndex++;
            }
        }
        catch (OrderInvalidStatusException e) {
            LOGGER.error("OrderInvalidStatusException thrown");
            sellOrders.remove(sellIndex);
            matchAndExecute(buyOrder, sellOrders, priceSpecification);
        }

        return executedOrders;
    }
}

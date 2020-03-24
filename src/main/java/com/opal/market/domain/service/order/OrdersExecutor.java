package com.opal.market.domain.service.order;

import com.opal.market.domain.models.PriceSpecification;
import com.opal.market.domain.models.market.OrderBook;
import com.opal.market.domain.models.order.Order;
import com.opal.market.domain.models.order.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrdersExecutor {

    private OrdersService ordersService;

    private Logger log = LoggerFactory.getLogger(OrdersExecutor.class);


    public OrdersExecutor(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    public int execute(Map<String, OrderBook> books) {
        Set<String> symbols = books.keySet();

        int totalExecuted = 0;

        for (String symbol : symbols) {
            totalExecuted += execute(books.get(symbol));
        }

        return totalExecuted;
    }

    public int execute(OrderBook orderBook) {
        List<Order> buyOrders  = orderBook.getBuyBook();
        List<Order> sellOrders = orderBook.getSellBook();

        ListIterator<Order> buyOrderListIterator = buyOrders.listIterator();
        PriceSpecification priceSpecification = new PriceSpecification();

        int totalExecuted = 0;

        while (buyOrderListIterator.hasNext()) {
            Order buyOrder = buyOrderListIterator.next();
            List<Order> executedOrders = ordersService.matchAndExecute(buyOrder, sellOrders, priceSpecification);

            if(buyOrder.getStatus() == OrderStatus.EXECUTED) {
                buyOrderListIterator.remove();
                totalExecuted++;
            }

            if(executedOrders.size()>0) {
                sellOrders.removeAll(executedOrders);
                totalExecuted += executedOrders.size();
                log.info("OrderExecuted:" + buyOrder);
            }
            else {
                break;
            }
        }

        return totalExecuted;
    }

    public Map<String, Boolean> hasMatch(Map<String, OrderBook> books) {
        Set<String> symbols = books.keySet();
        Map<String, Boolean> result = new HashMap<>();
        PriceSpecification priceSpecification = new PriceSpecification();

        for (String symbol : symbols) {
            OrderBook book = books.get(symbol);
            result.put(symbol, ordersService.hasMatch(book.getBuyBook(), book.getSellBook(), priceSpecification));
        }

        return result;
    }

}

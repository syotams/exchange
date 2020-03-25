package com.opal.market.domain.models.market;

import com.opal.market.domain.models.PriceSpecification;
import com.opal.market.domain.models.order.Order;
import com.opal.market.domain.models.order.OrderStatus;
import com.opal.market.domain.service.order.OrdersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Callable;

@Service
public class OrdersExecutor implements Callable<Integer> {

    private OrdersService ordersService;

    private Logger log = LoggerFactory.getLogger(OrdersExecutor.class);

    private OrderBook orderBook;


    public OrdersExecutor(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    public void setOrderBook(OrderBook orderBook) {
        this.orderBook = orderBook;
    }

    @Override
    public Integer call() {
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
}

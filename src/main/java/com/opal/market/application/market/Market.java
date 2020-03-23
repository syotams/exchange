package com.opal.market.application.market;

import com.opal.market.domain.models.MarketService;
import com.opal.market.domain.models.market.OrderBook;
import com.opal.market.domain.service.order.OrdersExecutor;
import com.opal.market.domain.models.order.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
public class Market implements MarketService {

    private Map<String, OrderBook> books = new HashMap<>();

    private final ArrayBlockingQueue<Order> ordersQueue = new ArrayBlockingQueue<>(100000);

    private OrdersExecutor ordersExecutor;

    private MarketStrategy strategy;

    private int totalReceived;

    private Logger log = LoggerFactory.getLogger(Market.class);


    public Market(@Autowired OrdersExecutor ordersExecutor, @Qualifier("intervalMarketStrategy") @Autowired MarketStrategy strategy) {
        this.ordersExecutor = ordersExecutor;
        this.strategy = strategy;
    }

    public void addOrder(Order order) throws InterruptedException {
        totalReceived++;
        ordersQueue.offer(order, 1000, TimeUnit.MILLISECONDS);
        log.info("OrderCreated:" + order);
    }

    public void immediatelyAddOrder(Order order) {
        if(!strategy.isRunning()) {
            getOrderBook(order.getEquity().getSymbol()).addOrder(order);
        }
    }

    public void addOrders(List<Order> orders) {
        for (Order order : orders) {
            getOrderBook(order.getEquity().getSymbol()).addOrder(order);
        }
    }

    public OrderBook getOrderBook(String symbol) {
        if(!books.containsKey(symbol)) {
            books.put(symbol, new OrderBook());
        }

        return books.get(symbol);
    }

    public Map<String, OrderBook> getBooks() {
        return books;
    }

    @PostConstruct
    public void start() {
        strategy.init(this, ordersQueue);
    }

    public void print() {
        Set<String> symbols = books.keySet();

        for (String symbol : symbols) {
            books.get(symbol).print();
        }
    }

    public void setRunning(boolean running) {
        strategy.setRunning(running);
    }

    @Override
    public void join(long time) throws InterruptedException {
        strategy.join(time);
    }

    public void execute() {
        ordersExecutor.execute(books);
    }

    public int getTotalReceived() {
        return totalReceived;
    }

    public int getTotalHandled() {
        return strategy.getTotalOrdersHandled();
    }
}

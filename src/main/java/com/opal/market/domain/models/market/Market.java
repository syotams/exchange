package com.opal.market.domain.models.market;

import com.opal.market.domain.models.PriceSpecification;
import com.opal.market.domain.models.order.Order;
import com.opal.market.domain.service.order.OrdersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;

@Component
public class Market {

    private Logger log = LoggerFactory.getLogger(Market.class);

    private final ExecutorService executorService;

    private final OrdersService ordersService;

    private final Map<String, OrderBook> books = new HashMap<>();

    private CompletionService<Integer> executor;

    private List<OrdersExecutor> ordersExecutors = new ArrayList<>();

    private int totalExecuted;

    private boolean isExecuting;

    private boolean bookInitiated;


    public Market(@Autowired OrdersService ordersService) {
        this.ordersService = ordersService;
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()-2);
        executor = new ExecutorCompletionService<>(executorService);
    }

    public void addOrder(Order order) {
        getOrderBook(order.getEquity().getSymbol()).addOrder(order);
    }

    public void addOrders(List<Order> orders) {
        for (Order order : orders) {
            addOrder(order);
        }
    }

    private OrderBook getOrderBook(String symbol) {
        if(!books.containsKey(symbol)) {
            books.put(symbol, new OrderBook());
            ordersExecutors.add(new OrdersExecutor(ordersService));
            bookInitiated = true;
        }

        return books.get(symbol);
    }

    public Map<String, String> stats() {
        Set<String> symbols = books.keySet();

        Map<String, String> stats = new HashMap<>();

        for (String symbol : symbols) {
            stats.put(symbol, String.format("%d, %d", books.get(symbol).getBuyBookSize(), books.get(symbol).getSellBookSize()));
        }

        stats.put("totalExecuted", String.valueOf(getTotalExecuted()));

        return stats;
    }

    public void print() {
        Set<String> symbols = books.keySet();

        for (String symbol : symbols) {
            books.get(symbol).print();
        }
    }

    public void execute() {
        if(!bookInitiated || isExecuting) {
            return;
        }

        Set<String> symbols = books.keySet();

        int index = 0;
        isExecuting = true;

        for (String symbol : symbols) {
            ordersExecutors.get(index).setOrderBook(books.get(symbol));
            executor.submit(ordersExecutors.get(index++));
        }

        int totalBooksExecuted = 0;

        try {
            while (isExecuting) {
                Future<Integer> future = executor.take();
                totalExecuted += future.get(10000, TimeUnit.MILLISECONDS);

                if(++totalBooksExecuted >= books.size()) {
                    isExecuting = false;
                }
            }
        }
        catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error(e.getMessage());
        } finally {
            isExecuting = false;
        }
    }

    public Map<String, Boolean> hasMatch() {
        Set<String> symbols = books.keySet();
        Map<String, Boolean> result = new HashMap<>();
        PriceSpecification priceSpecification = new PriceSpecification();

        for (String symbol : symbols) {
            OrderBook book = books.get(symbol);
            result.put(symbol, ordersService.hasMatch(book.getBuyBook(), book.getSellBook(), priceSpecification));
        }

        return result;
    }

    public Order[] getSellBook(String symbol) {
        return cloneOrders(getOrderBook(symbol).getSellBook());
    }

    public Order[] getBuyBook(String symbol) {
        return cloneOrders(getOrderBook(symbol).getBuyBook());
    }

    private Order[] cloneOrders(List<Order> book) {
        int total = Math.min(10, book.size());

        Order[] orders = new Order[total];

        for (int i=0; i<total; i++) {
            orders[i] = book.get(i);
        }

        return orders;
    }

    public void shutDown() throws InterruptedException {
        executorService.shutdownNow();
        executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);
    }

    public int getTotalExecuted() {
        return totalExecuted;
    }
}

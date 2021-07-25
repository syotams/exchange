package com.opal.market.domain.models.market;

import com.opal.market.domain.models.PriceSpecification;
import com.opal.market.domain.models.order.Order;
import com.opal.market.domain.service.order.OrdersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

public class Exchange {

    private final static Logger LOGGER = LoggerFactory.getLogger(Exchange.class);

    private final ExecutorService executorService;

    private final OrdersService ordersService;

    private final Map<String, OrderBook> books = new HashMap<>();

    private final CompletionService<Integer> executor;

    private final List<OrderBookOrdersExecutor> orderBookOrdersExecutors = new ArrayList<>();

    private int totalExecuted;

    private boolean isExecuting;


    public Exchange(OrdersService ordersService) {
        this.ordersService = ordersService;
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()-2);
        executor = new ExecutorCompletionService<>(executorService);
    }

    public void addOrder(Order order) {
        OrderBook orderBook = books.get(order.getEquity().getSymbol());
        if(null != orderBook) orderBook.addOrder(order);
    }

    public void addOrders(List<Order> orders) {
        for (Order order : orders) {
            addOrder(order);
        }
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

    public void execute() {
        if(isExecuting) {
            return;
        }
        isExecuting = true;
        Set<String> instruments = books.keySet();
        int index = 0;

        for (String instrument : instruments) {
            orderBookOrdersExecutors.get(index).setOrderBook(books.get(instrument));
            executor.submit(orderBookOrdersExecutors.get(index++));
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
            LOGGER.error(e.getMessage());
        } finally {
            isExecuting = false;
        }
    }

    // For performance test
    public Map<String, Boolean> hasMatch() {
        Set<String> instruments = books.keySet();
        Map<String, Boolean> result = new HashMap<>();
        PriceSpecification priceSpecification = new PriceSpecification();

        for (String instrument : instruments) {
            OrderBook book = books.get(instrument);
            result.put(instrument, ordersService.hasMatch(book.getBuyBook(), book.getSellBook(), priceSpecification));
        }

        return result;
    }

    public Order[] getSellBook(String symbol) {
        return cloneOrders(books.get(symbol).getSellBook());
    }

    public Order[] getBuyBook(String symbol) {
        return cloneOrders(books.get(symbol).getBuyBook());
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

    public void addInstruments(String[] equities) {
        for (String instrument : equities) {
            if(!books.containsKey(instrument)) {
                books.put(instrument, new OrderBook());
                orderBookOrdersExecutors.add(new OrderBookOrdersExecutor());
            }
        }
    }
}

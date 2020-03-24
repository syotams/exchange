package com.opal.market.domain.models.market;

import com.opal.market.domain.models.order.Order;
import com.opal.market.domain.service.order.OrdersExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Market {

    private Map<String, OrderBook> books = new HashMap<>();

    private OrdersExecutor ordersExecutor;

    private int totalExecuted;


    public Market(@Autowired OrdersExecutor ordersExecutor) {
        this.ordersExecutor = ordersExecutor;
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
        }

        return books.get(symbol);
    }

    protected Map<String, OrderBook> getBooks() {
        return books;
    }

    public String[] stats() {
        Set<String> symbols = books.keySet();
        String[] result = new String[symbols.size() + 1];

        int i=0;

        for (String symbol : symbols) {
            result[i++] = String.format("%s has %d in buy book and %d in sell book", symbol, books.get(symbol).getBuyBook().size(), books.get(symbol).getSellBook().size());
        }

        result[i] = String.format("Total %d orders executed buy and sell", getTotalExecuted());

        return result;
    }

    public void print() {
        Set<String> symbols = books.keySet();

        for (String symbol : symbols) {
            books.get(symbol).print();
        }
    }

    public void execute() {
        totalExecuted += ordersExecutor.execute(books);
    }

    public Map<String, Boolean> hasMatch() {
        return ordersExecutor.hasMatch(books);
    }

    public Order[] getSellBook(String symbol) {
        return cloneOrders(getOrderBook(symbol).getSellBook());
    }

    public Order[] getBuyBook(String symbol) {
        return cloneOrders(getOrderBook(symbol).getBuyBook());
    }

    private Order[] cloneOrders(List<Order> sellBook) {
        int total = Math.min(10, sellBook.size());

        Order[] orders = new Order[total];

        for (int i=0; i<total; i++) {
            orders[i] = sellBook.get(i);
        }

        return orders;
    }

    public int getTotalExecuted() {
        return totalExecuted;
    }
}

package com.opal.market.algorithms;


import com.opal.market.domain.models.market.OrderBook;
import com.opal.market.domain.models.order.Order;
import com.opal.market.domain.models.order.OrderComparator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

public class OrderBookPartitioned {

    private final OrderBook orderBookSingle;

    private final List<Order>[] orderBook;

    private final BigDecimal lastPrice = BigDecimal.valueOf(51);

    private final BigDecimal part1 = lastPrice.divide(BigDecimal.valueOf(1.05));
    private final BigDecimal part2 = lastPrice.divide(BigDecimal.valueOf(.95));


    public OrderBookPartitioned() {
        orderBook = new ArrayList[3];
        orderBook[0] = new ArrayList<>();
        orderBook[1] = new ArrayList<>();
        orderBook[2] = new ArrayList<>();
        orderBookSingle = new OrderBook();
    }

    public static void main(String[] args) {
        OrderBookPartitioned orderBookPartitioned = new OrderBookPartitioned();
        orderBookPartitioned.init();
    }

    public void init() {
        String[] symbols = getSymbols();
        MathContext mathContext = new MathContext(5);

        for(int i=0; i<1000000; i++) {
            int index = (int) (Math.random() * 16);
            BigDecimal bigDecimal50 = new BigDecimal((Math.random() * 2) + 50, mathContext);

            Order sellOrder = new Order(OrderSide.SELL, new Equity(symbols[index]), bigDecimal50, (int) (Math.random()*500+3), 1L);
            addOrder(sellOrder);
            orderBookSingle.addOrder(sellOrder);
        }

        long startTime = System.currentTimeMillis();
        List<Order> orderBook = getOrderBook();
        long endTime = System.currentTimeMillis();

        System.out.println("Elapsed time parti.: " + (endTime - startTime) + ", size: " + orderBook.size());

        startTime = System.currentTimeMillis();
        List<Order> sellBook = orderBookSingle.getSellBook();
        endTime = System.currentTimeMillis();

        System.out.println("Elapsed time single: " + (endTime - startTime) + ", size: " + sellBook.size());
    }

    private void addOrder(Order order) {
        if(order.getPrice().compareTo(lastPrice) >= 0) {
            orderBook[0].add(order);
        }
        else {
            orderBook[1].add(order);
        }
    }

    private List<Order> getOrderBook() {
        List<Order> books = new ArrayList<>(orderBook[0]);
        books.sort(new OrderComparator(SortDir.ASC));
//        books[1] = new ArrayList<>(orderBook[1]);
//        books[1].sort(new OrderComparator(SortDir.ASC));

        return books;
    }

    private String[] getSymbols() {
        int matrixRows = 4;

        String[] symbols = new String[matrixRows*matrixRows];

        for(int x=0; x<matrixRows; x++) {
            char c1 = (char)(0x41+x);

            int row = matrixRows * x;

            for(int y=0; y<matrixRows; y++) {
                char c2 = (char)(0x41+y);
                symbols[row+y] = String.valueOf(c1) + c2;
            }
        }

        return symbols;
    }
}

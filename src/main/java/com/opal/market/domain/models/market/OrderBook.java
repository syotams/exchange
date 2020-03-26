package com.opal.market.domain.models.market;

import com.opal.market.domain.models.SORT_DIR;
import com.opal.market.domain.models.order.Order;
import com.opal.market.domain.models.order.OrderComparator;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Order book holds all orders ordered by price and time
 *
 */
public class OrderBook {

    private List<Order> buyBook = new ArrayList<>();

    private List<Order> sellBook = new ArrayList<>();

    private OrderComparator buyBookComparator;

    private OrderComparator sellBookComparator;


    public OrderBook() {
        buyBookComparator  = new OrderComparator(SORT_DIR.DESC);
        sellBookComparator = new OrderComparator(SORT_DIR.ASC);
    }

    public void addOrder(Order order) {
        switch (order.getSide()) {
            case BUY:
                buyBook.add(order);
                break;
            case SELL:
                sellBook.add(order);
                break;
            default:
                throw new InvalidParameterException();
        }
    }

    public List<Order> getBuyBook() {
        buyBook.sort(buyBookComparator);
        return buyBook;
    }

    public List<Order> getSellBook() {
        sellBook.sort(sellBookComparator);
        return sellBook;
    }

    public int getBuyBookSize() {
        return buyBook.size();
    }

    public int getSellBookSize() {
        return sellBook.size();
    }

    public void print() {
        for (Order order : buyBook) {
            System.out.println(order);
        }

        System.out.println("**********************");

        for (Order order : sellBook) {
            System.out.println(order);
        }
    }
}

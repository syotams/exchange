package com.opal.market.domain.models.order;

import com.opal.market.domain.models.PriceComparator;
import com.opal.market.domain.models.SORT_DIR;
import com.opal.market.domain.models.TimeComparator;

import java.util.Comparator;

public class OrderComparator implements Comparator<Order> {

    private PriceComparator priceComparator;

    private TimeComparator timeComparator;


    public OrderComparator(SORT_DIR dir) {
        priceComparator = new PriceComparator(dir);
        timeComparator = new TimeComparator(dir);
    }

    @Override
    public int compare(Order order1, Order order2) {
        int result;

        result = priceComparator.compare(order1.getPrice(), order2.getPrice());

        if(0==result) {
           result = timeComparator.compare(order1.getCreatedTime(), order2.getCreatedTime());
        }

        return result;
    }

}

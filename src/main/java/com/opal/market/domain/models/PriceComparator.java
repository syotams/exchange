package com.opal.market.domain.models;

import java.math.BigDecimal;
import java.util.Comparator;

public class PriceComparator implements Comparator<BigDecimal> {

    private SortDir dir;


    public PriceComparator(SortDir dir) {
        this.dir = dir;
    }

    @Override
    public int compare(BigDecimal price1, BigDecimal price2) {
        int result = 0;

        if(price1.compareTo(price2) > 0) {
            result = dir == SortDir.ASC ? 1 : -1;
        }
        else if(price1.compareTo(price2) < 0) {
            result = dir == SortDir.DESC ? 1 : -1;
        }

        return result;
    }

}

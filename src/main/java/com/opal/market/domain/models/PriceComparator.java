package com.opal.market.domain.models;

import java.math.BigDecimal;
import java.util.Comparator;

public class PriceComparator implements Comparator<BigDecimal> {

    private SORT_DIR dir;


    public PriceComparator(SORT_DIR dir) {
        this.dir = dir;
    }

    @Override
    public int compare(BigDecimal price1, BigDecimal price2) {
        int result = 0;

        if(price1.compareTo(price2) > 0) {
            result = dir == SORT_DIR.ASC ? 1 : -1;
        }
        else if(price1.compareTo(price2) < 0) {
            result = dir == SORT_DIR.DESC ? 1 : -1;
        }

        return result;
    }

}

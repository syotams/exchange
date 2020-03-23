package com.opal.market.domain.models;

import java.util.Comparator;

public class TimeComparator implements Comparator<Long> {

    private SORT_DIR dir;


    public TimeComparator(SORT_DIR dir) {
        this.dir = dir;
    }

    @Override
    public int compare(Long time1, Long time2) {
        int result = 0;

        if(time1 > time2) {
            result = dir == SORT_DIR.ASC ? 1 : -1;
        }
        else if(time1 < time2) {
            result = dir == SORT_DIR.DESC ? 1 : -1;
        }

        return result;
    }
}

package com.opal.market.domain.models;

import java.util.Comparator;

public class TimeComparator implements Comparator<Long> {

    private SortDir dir;


    public TimeComparator(SortDir dir) {
        this.dir = dir;
    }

    @Override
    public int compare(Long time1, Long time2) {
        int result = 0;

        if(time1 > time2) {
            result = dir == SortDir.ASC ? 1 : -1;
        }
        else if(time1 < time2) {
            result = dir == SortDir.DESC ? 1 : -1;
        }

        return result;
    }
}

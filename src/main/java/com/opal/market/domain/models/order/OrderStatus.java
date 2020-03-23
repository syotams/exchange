package com.opal.market.domain.models.order;

public interface OrderStatus {

    byte OPENED = 0x1;
    byte PARTIALLY_EXECUTED = 0x2;
    byte EXECUTED = 0x3;
    byte CANCELED = 0x4;

    static String toString(byte status) {
        switch (status) {
            case OPENED:
                return "OPENED";
            case PARTIALLY_EXECUTED:
                return "PARTIALLY_EXECUTED";
            case EXECUTED:
                return "EXECUTED";
            case CANCELED:
                return "CANCELED";
            default:
                return null;
        }
    }

}

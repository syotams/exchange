package com.opal.market.application.market;

public interface IQueue<T> {
    void addItem(T item) throws InterruptedException;

    void setRunning(boolean isRunning);

    boolean isRunning();

    int getTotalHandled();

    int getTotalReceived();
}

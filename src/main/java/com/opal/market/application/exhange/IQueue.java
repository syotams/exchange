package com.opal.market.application.exhange;

public interface IQueue<T> {
    void addItem(T item) throws InterruptedException;

    void addItems(T[] item) throws InterruptedException;
}

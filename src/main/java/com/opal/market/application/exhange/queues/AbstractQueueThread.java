package com.opal.market.application.exhange.queues;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public abstract class AbstractQueueThread<T> extends Thread implements IExchangeQueue<T> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    protected final BlockingQueue<T> queue = new ArrayBlockingQueue<>(1000000);

    protected int totalHandled;

    protected int totalReceived;

    protected volatile boolean isRunning;

    public AbstractQueueThread(String name) {
        super(name);
    }

    @PostConstruct
    public void init() {
        start();
    }

    @Override
    public void addItem(T item) throws InterruptedException {
        if (queue.offer(item, 1000, TimeUnit.MILLISECONDS)) {
            totalReceived++;
            log.info("OrderCreated:" + item);
        }
    }

    @Override
    public void addItems(T[] item) {
        if (queue.addAll(Arrays.asList(item))) {
            totalReceived+=item.length;
            log.info("Multiple OrderCreated:" + item.length);
        }
    }

    @Override
    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    @Override
    public boolean isRunning() {
        return this.isRunning;
    }

    @Override
    public int getTotalHandled() {
        return totalHandled;
    }

    @Override
    public int getTotalReceived() {
        return totalReceived;
    }
}

package com.opal.market.application.market;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public abstract class AbstractQueueThread<T> extends Thread implements IQueue<T> {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    protected final ArrayBlockingQueue<T> queue = new ArrayBlockingQueue<>(50000);

    protected int totalHandled;

    protected int totalReceived;

    protected boolean isRunning;

    @PostConstruct
    public void init() {
        setName(getClass().getSimpleName());
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

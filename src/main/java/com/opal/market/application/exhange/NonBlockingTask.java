package com.opal.market.application.exhange;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

public class NonBlockingTask<T> {

    private volatile boolean isCompleted;

    private final Callable<T> callable;

    private T result;


    public NonBlockingTask(Callable<T> callable) {
        this.callable = callable;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public void run() {
        try {
            setResult(callable.call());
        } catch (Exception e) {
            e.printStackTrace();
        }
        setCompleted(true);
    }

    public T get() {
        while (!isCompleted());
        return getResult();
    }

    public T get(int timeout) throws InterruptedException, TimeoutException {
        long timeElapsed = 0;
        long startTime = System.currentTimeMillis();

        while (!isCompleted() && timeElapsed < timeout) {
            Thread.sleep(10);
            timeElapsed = System.currentTimeMillis() - startTime;
        }

        if(timeElapsed > timeout && !isCompleted()) {
            throw new TimeoutException();
        }

        return getResult();
    }

    private synchronized void setResult(T result) {
        this.result = result;
    }

    private synchronized T getResult() {
        return result;
    }
}

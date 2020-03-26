package com.opal.market;

import com.opal.market.application.market.IntervalMarketQueueThread;
import com.opal.market.application.market.NonBlockingTask;
import com.opal.market.domain.models.market.Market;
import com.opal.market.domain.service.order.OrdersService;

import java.util.*;
import java.util.concurrent.*;

import static java.lang.Thread.sleep;

public class PerformanceTest {

    private final OrdersService ordersService;

    private IntervalMarketQueueThread intervalMarketQueueThread;

    private int sleepTime = 1000;

    public PerformanceTest() {
        ordersService = new OrdersService();
        intervalMarketQueueThread = new IntervalMarketQueueThread(new Market(ordersService));
    }

    public static void main(String[] args) throws InterruptedException {
        new PerformanceTest().run();
    }

    public void run() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        intervalMarketQueueThread.start();
        startMetrics();

        List<Callable<Long>> tasks = new ArrayList<>();

        int numberOfConcurrentFeeds = 3;

        for(int i = 0; i< numberOfConcurrentFeeds; i++) {
            tasks.add(new OrderMockCreator(intervalMarketQueueThread));
        }

        ExecutorService executorService = Executors.newFixedThreadPool(tasks.size());
        CompletionService<Long> completionService = new ExecutorCompletionService<>(executorService);
        for (Callable<Long> task : tasks) {
            completionService.submit(task);
        }

        boolean isExecuting = true;
        int totalBooksExecuted = 0;

        try {
            while (isExecuting) {
                Future<Long> future = completionService.take();
                future.get();

                if(++totalBooksExecuted >= tasks.size()) {
                    isExecuting = false;
                }
            }
        }
        catch (InterruptedException | ExecutionException e) {}
        finally {
            executorService.shutdown();
        }

        long elapsedTime = 0;

        elapsedTime = System.currentTimeMillis() - startTime;

        System.out.println("--------------------------- Adding " + intervalMarketQueueThread.getTotalReceived() + " orders elapsed time: " + (elapsedTime));

        int matches;
        int i=0;

        System.out.println("--------------------------- Waiting for orders to be executed ----------");

        do {
            matches = 0;

            intervalMarketQueueThread.join(sleepTime);

            NonBlockingTask<Map<String, Boolean>> mapNonBlockingTask = intervalMarketQueueThread.hasMatch();
            Map<String, Boolean> stringBooleanMap = mapNonBlockingTask.get();
            Set<String> symbols = stringBooleanMap.keySet();

            for (String symbol : symbols) {
                if(stringBooleanMap.get(symbol)) {
                    matches++;
                }
            }
        } while (matches > 0);

        System.out.println(intervalMarketQueueThread.stats().get());
        intervalMarketQueueThread.setRunning(false);

        System.out.println("--------------------------- Elapsed time: " + (System.currentTimeMillis() - startTime));
    }

    private void startMetrics() {
        List<Integer> ordersPerSecond = new ArrayList<>();

        new Thread(() -> {
            try {
                int last = 0;

                do {
                    sleep(sleepTime);

                    System.out.println(intervalMarketQueueThread.stats().get());

                    int totalReceived = intervalMarketQueueThread.getTotalHandled();

                    if(totalReceived > 0) {
                        ordersPerSecond.add(totalReceived - last);
                        last = totalReceived;
                    }
                } while (intervalMarketQueueThread.isRunning());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

//            System.out.println("------------- Market received " + marketQueue.getTotalReceived() + " orders -----------");
            System.out.println(ordersPerSecond);
        }).start();
    }
}

package com.opal.market;

import com.opal.market.application.market.IntervalMarketQueueThread;
import com.opal.market.application.market.NonBlockingTask;
import com.opal.market.domain.models.PriceSpecification;
import com.opal.market.domain.models.market.Market;
import com.opal.market.domain.models.order.Order;
import com.opal.market.domain.models.order.OrderSide;
import com.opal.market.domain.models.market.OrdersExecutor;
import com.opal.market.domain.service.order.OrdersService;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class MarketMockApplication {

    private final OrdersService ordersService;

    private IntervalMarketQueueThread marketQueue;

    private int sleepTime = 1000;

    public MarketMockApplication() {
        ordersService = new OrdersService();
        marketQueue = new IntervalMarketQueueThread(new Market(ordersService));
    }

    public static void main(String[] args) throws InterruptedException {
        new MarketMockApplication().run();
    }

    public void run() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        marketQueue.start();
        startMetrics();

        List<Callable<Long>> tasks = new ArrayList<>();
        tasks.add(new OrderMockCreator(marketQueue));
//        tasks.add(new OrderMockCreator(marketQueue));
//        tasks.add(new OrderMockCreator(marketQueue));
        ExecutorService executorService = Executors.newFixedThreadPool(tasks.size());
        executorService.invokeAll(tasks);

        long elapsedTime = 0;

        executorService.shutdown();
        executorService.awaitTermination(20, TimeUnit.SECONDS);
        elapsedTime = System.currentTimeMillis() - startTime;

        System.out.println("--------------------------- Adding " + marketQueue.getTotalReceived() + " orders elapsed time: " + (elapsedTime));

        int matches;
        int i=0;

        System.out.println("--------------------------- Waiting for orders to be executed ----------");

        do {
            matches = 0;

            marketQueue.join(sleepTime);

            NonBlockingTask<Map<String, Boolean>> mapNonBlockingTask = marketQueue.hasMatch();
            Map<String, Boolean> stringBooleanMap = mapNonBlockingTask.get();
            Set<String> symbols = stringBooleanMap.keySet();

            for (String symbol : symbols) {
                if(stringBooleanMap.get(symbol)) {
                    matches++;
                }
            }
        } while (matches > 0 && i++<300);

        marketQueue.stats();
        marketQueue.setRunning(false);

        System.out.println("--------------------------- Elapsed time: " + (System.currentTimeMillis() - startTime));
    }

    private void startMetrics() {
        List<Integer> ordersPerSecond = new ArrayList<>();

        new Thread(() -> {
            try {
                int last = 0;

                do {
                    sleep(sleepTime);

                    marketQueue.stats();

                    int totalReceived = marketQueue.getTotalHandled();

                    if(totalReceived > 0) {
                        ordersPerSecond.add(totalReceived - last);
                        last = totalReceived;
                    }
                } while (marketQueue.isRunning());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("------------- Market received " + marketQueue.getTotalReceived() + " orders -----------");
            System.out.println(ordersPerSecond);
        }).start();
    }
}

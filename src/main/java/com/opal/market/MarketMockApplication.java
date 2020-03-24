package com.opal.market;

import com.opal.market.application.market.IntervalMarketQueueThread;
import com.opal.market.application.market.NonBlockingTask;
import com.opal.market.domain.models.PriceSpecification;
import com.opal.market.domain.models.market.Market;
import com.opal.market.domain.models.market.OrderBook;
import com.opal.market.domain.models.order.Order;
import com.opal.market.domain.models.order.OrderSide;
import com.opal.market.domain.service.order.OrdersExecutor;
import com.opal.market.domain.service.order.OrdersService;

import java.time.format.SignStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class MarketMockApplication {

    private final OrdersService ordersService;

    private IntervalMarketQueueThread marketQueue;


    public MarketMockApplication() {
        ordersService = new OrdersService();
        OrdersExecutor ordersExecutor = new OrdersExecutor(ordersService);
        marketQueue = new IntervalMarketQueueThread(new Market(ordersExecutor));
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

        System.out.println("--------------------------- Elapsed time: " + elapsedTime);

        boolean matches;
        int i=0;
        List<Order> buyBook;
        List<Order> sellBook;

        do {
            marketQueue.stats();

            marketQueue.join(1000);

            NonBlockingTask<Order[]> task = marketQueue.getOrderBook("LRCX", OrderSide.BUY);
            buyBook = Arrays.asList(task.get());

            task = marketQueue.getOrderBook("LRCX", OrderSide.SELL);
            sellBook = Arrays.asList(task.get());

            matches = ordersService.hasMatch(buyBook, sellBook, new PriceSpecification());
        } while (matches && i++<300);

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
                    sleep(5000);

                    marketQueue.stats();

                    int totalReceived = marketQueue.getTotalHandled();
                    ordersPerSecond.add(totalReceived - last);
                    last = totalReceived;

                    System.out.println(String.format("Market handled %d orders last 5 second", last));
                } while (marketQueue.isRunning());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("------------- Market received " + marketQueue.getTotalReceived() + " orders -----------");
            System.out.println(ordersPerSecond);
        }).start();
    }
}

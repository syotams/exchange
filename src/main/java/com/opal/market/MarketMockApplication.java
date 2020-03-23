package com.opal.market;

import com.opal.market.domain.models.market.OrderBook;
import com.opal.market.domain.models.market.IntervalMarketStrategy;
import com.opal.market.application.market.Market;
import com.opal.market.domain.models.MarketService;
import com.opal.market.domain.service.order.OrdersExecutor;
import com.opal.market.domain.service.order.OrdersService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class MarketMockApplication {

    private MarketService market;

    private OrdersExecutor ordersExecutor;


    public MarketMockApplication() {
        ordersExecutor = new OrdersExecutor(new OrdersService());
        market = new Market(ordersExecutor, new IntervalMarketStrategy());
    }

    public static void main(String[] args) throws InterruptedException {
        new MarketMockApplication().run();
    }

    public void run() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        market.start();
//        startMetrics();

        List<Callable<Long>> tasks = new ArrayList<>();
        tasks.add(new OrderMockCreator(market));
//        tasks.add(new OrderMockCreator(market));
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.invokeAll(tasks);

        long elapsedTime = 0;

        executorService.shutdown();
        executorService.awaitTermination(20, TimeUnit.SECONDS);
        elapsedTime = System.currentTimeMillis() - startTime;

        market.join(3000);

        System.out.println("--------------------------- Elapsed time: " + elapsedTime);
        market.setRunning(false);

        sleep(3000);

        Map<String, Boolean> matches = ordersExecutor.hasMatch(market.getBooks());

        for (String symbol : market.getBooks().keySet()) {
            OrderBook orderBook = market.getOrderBook(symbol);
            System.out.println(String.format("------------- Orders book %s size sell %d, buy %d", symbol, orderBook.getSellBook().size(), orderBook.getBuyBook().size()));
            System.out.println(String.format("Book %s %s matches", symbol, matches.get(symbol).toString()));
            orderBook.print();
        }
    }

    private void startMetrics() {
        List<Integer> ordersPerSecond = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        new Thread(() -> {
            try {
                int last = 0;

                do {
                    sleep(1000);

                    int totalReceived = market.getTotalReceived();
                    ordersPerSecond.add(totalReceived - last);
                    last = totalReceived;

                    System.out.println(last);
                } while (System.currentTimeMillis() - startTime < 10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("------------- Market closed and received " + market.getTotalReceived() + " orders -----------");
            System.out.println(ordersPerSecond);
        }).start();
    }
}

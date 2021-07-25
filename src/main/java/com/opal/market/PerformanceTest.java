package com.opal.market;

import com.opal.market.application.exhange.queues.IntervalExchangeQueueThread;
import com.opal.market.application.exhange.NonBlockingTask;
import com.opal.market.domain.models.market.Exchange;
import com.opal.market.domain.service.order.OrdersService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

import static java.lang.Thread.sleep;

public class PerformanceTest {

    private final OrdersService ordersService;

    private IntervalExchangeQueueThread intervalExchangeQueueThread;

    private final int sleepTime = 1000;

    private int numberOfBrokers = 10;

    private int numberOfIter = 1000;

    private int numberPerIter = 100;


    private String[] instruments = new String[] {"LRCX", "NVDA", "APPL", "GE", "BA", "MU", "TSEM", "AIG", "MSFT", "TSLA"};

    public PerformanceTest() {
        ordersService = new OrdersService();
        Exchange exchange = new Exchange(ordersService);
        exchange.addInstruments(instruments);
        intervalExchangeQueueThread = new IntervalExchangeQueueThread(exchange);
    }

    public static void main(String[] args) throws InterruptedException {
        PerformanceTest performanceTest = new PerformanceTest();
        if(args.length > 0) {
            performanceTest.numberOfBrokers = Integer.parseInt(args[0]);
        }
        if(args.length > 1) {
            performanceTest.numberOfIter = Integer.parseInt(args[1]);
        }
        if(args.length > 2) {
            performanceTest.numberPerIter = Integer.parseInt(args[2]);
        }
        System.out.format("numberOfBrokers: %d, numberOfIter: %s, numberPerIter: %s%n",
                performanceTest.numberOfBrokers,
                performanceTest.numberOfIter,
                performanceTest.numberPerIter);
        performanceTest.run();
    }

    public void run() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        intervalExchangeQueueThread.start();
        //startMetrics();

        List<Callable<Long>> tasks = new ArrayList<>();

        for(int i = 0; i< numberOfBrokers; i++) {
            tasks.add(new OrderMockCreator(intervalExchangeQueueThread, instruments, numberOfIter, numberPerIter));
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
        catch (InterruptedException | ExecutionException e) {
            System.out.println(e.getMessage());
        }
        finally {
            executorService.shutdown();
        }

        long elapsedTime = 0;

        elapsedTime = System.currentTimeMillis() - startTime;

        System.out.println("--------------------------- Adding " + intervalExchangeQueueThread.getTotalReceived() + " orders elapsed time: " + (elapsedTime));

        int matches;
        int i=0;

        System.out.println("--------------------------- Waiting for orders to be executed ----------");

        do {
            matches = 0;

            intervalExchangeQueueThread.join(sleepTime);

            NonBlockingTask<Map<String, Boolean>> mapNonBlockingTask = intervalExchangeQueueThread.hasMatch();
            Map<String, Boolean> stringBooleanMap = mapNonBlockingTask.get();
            Set<String> symbols = stringBooleanMap.keySet();

            for (String symbol : symbols) {
                if(stringBooleanMap.get(symbol)) {
                    matches++;
                }
            }
        } while (matches > 0);

        System.out.println(intervalExchangeQueueThread.stats().get());
        intervalExchangeQueueThread.setRunning(false);

        System.out.println("--------------------------- Elapsed time: " + (System.currentTimeMillis() - startTime));
    }

    private void startMetrics() {
        List<Integer> ordersPerSecond = new ArrayList<>();

        new Thread(() -> {
            try {
                int last = 0;

                do {
                    sleep(sleepTime);

                    System.out.println(intervalExchangeQueueThread.stats().get());

                    int totalReceived = intervalExchangeQueueThread.getTotalHandled();

                    if(totalReceived > 0) {
                        ordersPerSecond.add(totalReceived - last);
                        last = totalReceived;
                    }
                } while (intervalExchangeQueueThread.isRunning());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("------------- Market received " + ordersPerSecond + " orders / sec -----------");
//            System.out.println(ordersPerSecond);
        }, "PerformanceMetrics").start();
    }
}

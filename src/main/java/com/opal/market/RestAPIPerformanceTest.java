package com.opal.market;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.opal.market.domain.models.order.OrderSide;
import com.opal.market.interfaces.api.CreateOrderCommand;
import org.springframework.beans.factory.annotation.Value;

import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.*;

public class RestAPIPerformanceTest implements Callable<Integer> {

    private static int numberOfIterations = 10;

    private static int numberOfWorkers = 16;

    private static long timeToRun = 60000;

    private static boolean useTime = false;

    private String name;


    public RestAPIPerformanceTest(String name) {
        this.name = name;
    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfWorkers);
        CompletionService<Integer> completionService = new ExecutorCompletionService<>(executorService);

        for(int i=0; i<numberOfWorkers; i++) {
            completionService.submit(new RestAPIPerformanceTest("Worker " + i));
        }

        long startTime = System.currentTimeMillis();

        boolean isExecuting = true;
        int totalSent = 0, index = 0;

        try {
            while (isExecuting) {
                Future<Integer> future = completionService.take();
                totalSent += future.get();

                if(++index >= numberOfWorkers) {
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
        System.out.println("--------------------------- Elapsed time: " + (System.currentTimeMillis() - startTime) + ", sent " + totalSent);
    }


    @Override
    public Integer call() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        String[] equities = new String[] {"LRCX", "MU", "BA", "GE", "AIG", "TEVA", "TSLA", "FRSX", "INTC", "BAC", "NVDA"};

        int i=0;
        boolean shouldRun;
        long startTime = System.currentTimeMillis();

        do {
            HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8080/api/v1.0/orders/").openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());

            BigDecimal bigDecimal50 = BigDecimal.valueOf((long) ((Math.random() * 5) + 50));
            int quantity = (int) (Math.random() * 500 + 1);
            OrderSide side = Math.random() > 0.5 ? OrderSide.SELL : OrderSide.BUY;

            CreateOrderCommand command = new CreateOrderCommand();
            command.setSymbol(equities[(int) (Math.random()*equities.length-1)]);
            command.setPrice(bigDecimal50);
            command.setQuantity(quantity);
            command.setUserId((long) (Math.random() * 2 + 1));
            command.setSide(side);

            String postData = mapper.writeValueAsString(command);
            wr.write(postData);
            wr.flush();

            int responseCode = connection.getResponseCode();

            if(responseCode >= 200 && responseCode <=201){
                System.out.format("POST %-9s:%d was successful.%n", name, i);
            }
            else if(responseCode == 401){
                System.out.println("Wrong password.");
            }

            if(useTime) {
                shouldRun = (System.currentTimeMillis() - startTime) < timeToRun;
            }
            else {
                shouldRun = ++i < numberOfIterations;
            }
//            Thread.sleep(100);
        }
        while (shouldRun);

        return i;
    }
}

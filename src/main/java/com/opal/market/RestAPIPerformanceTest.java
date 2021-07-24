package com.opal.market;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.opal.market.domain.models.order.OrderSide;
import com.opal.market.interfaces.api.CreateOrderCommand;

import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class RestAPIPerformanceTest implements Callable<Integer> {

    private static int matrixRows = 9;

    private static int numberOfIterations = 1;

    private static int numberOfWorkers = 5000;

    private static long timeToRun = 10000;

    private static boolean useTime = false;


    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfWorkers);

        System.out.println("------------------------------------------");

        if(useTime) {
            System.out.println(String.format("Starting test with: %d users for %dms, %d stocks",
                    numberOfWorkers, timeToRun, matrixRows*matrixRows));
        }
        else {
            System.out.println(String.format("Starting test with: %d users %d iterations, %d stocks",
                    numberOfWorkers, numberOfIterations, matrixRows*matrixRows));
        }

        List<RestAPIPerformanceTest> callers = new ArrayList<>();

        for(int i=0; i<numberOfWorkers; i++) {
            RestAPIPerformanceTest restAPIPerformanceTest = new RestAPIPerformanceTest();
            callers.add(restAPIPerformanceTest);
        }

        boolean isExecuting = true;
        int totalSent = 0, index = 0;
        long startTime = System.currentTimeMillis();

        try {
            List<Future<Integer>> futures = executorService.invokeAll(callers);

            while (isExecuting) {
                Future<Integer> future = futures.get(index);
                totalSent += future.get();

                if(++index >= numberOfWorkers) {
                    isExecuting = false;
                }
            }
        }
        catch (ExecutionException e) {
            System.out.println("Execution exception: " + e.getMessage());
        }
        catch (InterruptedException e) {
            System.out.println("Interrupted exception: " + e.getMessage());
        }
        finally {
            System.out.println("Finished workers shutting down");
            executorService.shutdown();
        }
        System.out.println("Elapsed time: " + (System.currentTimeMillis() - startTime) + ", sent " + totalSent);
    }

    @Override
    public Integer call() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        String[] symbols = getSymbols();

        int numberOfExecutions=0;
        boolean shouldRun;
        long startTime = System.currentTimeMillis();

        do {
            HttpURLConnection connection =
                    (HttpURLConnection) new URL("http://localhost:8080/api/v1.0/orders/").openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());

            BigDecimal bigDecimal50 = BigDecimal.valueOf((long) ((Math.random() * 5) + 50));
            int quantity = (int) (Math.random() * 500 + 1);
            OrderSide side = Math.random() > 0.5 ? OrderSide.SELL : OrderSide.BUY;

            CreateOrderCommand command = new CreateOrderCommand();
            command.setSymbol(symbols[(int) (Math.random()*symbols.length-1)]);
            command.setPrice(bigDecimal50);
            command.setQuantity(quantity);
            command.setUserId((long) (Math.random() * 2 + 1));
            command.setSide(side);

            String postData = mapper.writeValueAsString(command);
            wr.write(postData);
            wr.flush();

            int responseCode = connection.getResponseCode();
            connection.disconnect();

            if(responseCode == 200){
                System.out.println("POST was successful.");
            }
            else if(responseCode == 401){
                System.out.println("Wrong password.");
            }

            numberOfExecutions++;

            if(useTime) {
                shouldRun = (System.currentTimeMillis() - startTime) < timeToRun;
            }
            else {
                shouldRun = numberOfExecutions < numberOfIterations;
            }
        }
        while (shouldRun);

        return numberOfExecutions;
    }

    private String[] getSymbols() {
        String[] symbols = new String[matrixRows*matrixRows];

        for(int x=0; x<matrixRows; x++) {
            char c1 = (char)(0x41+x);

            int row = matrixRows * x;

            for(int y=0; y<matrixRows; y++) {
                char c2 = (char)(0x41+y);
                symbols[row+y] = String.valueOf(c1) + c2;
            }
        }

        return symbols;
    }
}

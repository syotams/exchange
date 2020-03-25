package com.opal.market.application.market;

import com.opal.market.domain.models.market.Market;
import com.opal.market.domain.models.order.Order;
import com.opal.market.domain.models.order.OrderSide;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;


@Component
public class IntervalMarketQueueThread extends AbstractQueueThread<Order> implements IMarketQueue {

    private final Market market;

    private BlockingQueue<NonBlockingTask> tasks = new ArrayBlockingQueue<>(100);


    public IntervalMarketQueueThread(@Autowired Market market) {
        this.market = market;
    }

    @Override
    public void run() {
        List<Order> orders = new ArrayList<>();
        setRunning(true);
        int size;

        while (isRunning) {
            queue.drainTo(orders);

            if((size = orders.size()) > 0) {
                market.addOrders(orders);
                totalHandled += size;
                orders.clear();
            }

            market.execute();

            if(tasks.size() > 0) {
                tasks.poll().run();
            }
        }

        try {
            market.shutDown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("------------- Market closed and received " + totalReceived + " and handled " + totalHandled + " orders -----------");
        System.out.println(Arrays.toString(market.stats()));
    }

    @Override
    public NonBlockingTask<Order[]> getOrderBook(String symbol, OrderSide side) {
        NonBlockingTask<Order[]> task = new NonBlockingTask<>(() -> {
            switch(side) {
                case SELL:
                    return market.getSellBook(symbol);
                default:
                case BUY:
                    return market.getBuyBook(symbol);
            }
        });
        tasks.add(task);
        return task;
    }

    public NonBlockingTask<Null> stats() {
        NonBlockingTask<Null> task = new NonBlockingTask<Null>(() -> {
            System.out.println(Arrays.toString(market.stats()));
            return null;
        });
        tasks.add(task);
        return task;
    }

    public NonBlockingTask<Map<String, Boolean>> hasMatch() {
        NonBlockingTask<Map<String, Boolean>> task = new NonBlockingTask<>(market::hasMatch);
        tasks.add(task);
        return task;
    }
}

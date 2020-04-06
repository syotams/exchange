package com.opal.market.application.market;

import com.opal.market.domain.models.market.Market;
import com.opal.market.domain.models.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Lazy
public class ImmediateMarketQueueThread extends AbstractQueueThread<Order> {

    private final Market market;


    public ImmediateMarketQueueThread(@Autowired Market market) {
        this.market = market;
    }

    @Override
    public void run() {
        setRunning(true);
        Order order;

        try {
            int i=1;

            while (isRunning) {
                if (null != (order = queue.poll(5, TimeUnit.MILLISECONDS))) {
                    market.addOrder(order);
                    totalHandled++;

                    // execute every 50 iterations ~ 50 * poll time = 250ms
                    if((i++ % 50) == 0) {
                        market.execute();
                        i=1;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("------------- Market closed and served " + totalHandled +" orders -----------");
    }

}

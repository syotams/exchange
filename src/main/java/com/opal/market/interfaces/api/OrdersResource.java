package com.opal.market.interfaces.api;

import com.opal.market.application.market.OrdersApplicationService;
import com.opal.market.domain.models.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1.0/orders")
public class OrdersResource {

    private final OrdersApplicationService ordersApplicationService;


    public OrdersResource(@Autowired OrdersApplicationService ordersApplicationService) {
        this.ordersApplicationService = ordersApplicationService;
    }

    @GetMapping("/{id}")
    public Order get(@PathVariable(value = "id") String id) {
        return null;
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public Order create(@RequestBody CreateOrderCommand createOrderCommand) throws InterruptedException {
        return ordersApplicationService.createOrder(
                createOrderCommand.getUserId(),
                createOrderCommand.getSymbol(),
                createOrderCommand.getSide(),
                createOrderCommand.getPrice(),
                createOrderCommand.getQuantity()
        );
    }

    public void update() {
        return;
    }

    public void delete() {
        return;
    }

}

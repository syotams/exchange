package com.opal.market.domain.models.order;

import com.opal.market.domain.models.equity.Equity;
import com.opal.market.domain.shared.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Order {

    private String id;
    
    private Equity equity;

    private BigDecimal price;

    private int quantity;

    private long createdTime;

    private byte status;

    private Long userId;

    private OrderSide side;

    private List<Execution> executions;


    public Order() {
        id = UUID.randomUUID().toString();
        createdTime = System.nanoTime();
        status = OrderStatus.OPENED;
        executions = new ArrayList<>();
    }

    public Order(OrderSide side, Equity equity, BigDecimal price, int quantity, Long userId) {
        this();
        this.side = side;
        this.equity = equity;
        this.price = price;
        this.quantity = quantity;
        this.userId = userId;
    }

    public Execution trade(Order buyOrder, Specification<BigDecimal> priceSpecification) throws OrderInvalidStatusException, OrdersDoesNotMatchException {
        if(priceSpecification.isSatisfiedBy(getPrice())) {
            int totalQuantity = Math.min(getRemainingQuantity(), buyOrder.getRemainingQuantity());

            Execution execution = new Execution(getId(), buyOrder.getId(), totalQuantity, getPrice());

            buyOrder.addExecution(execution);
            addExecution(execution);

            return execution;
        }
        else {
            throw new OrdersDoesNotMatchException();
        }
    }

    private void addExecution(Execution execution) throws OrderInvalidStatusException {
        if(status == OrderStatus.CANCELED) {
            throw new OrderInvalidStatusException("Order is " + status);
        }

        if(status == OrderStatus.EXECUTED) {
            throw new OrderInvalidStatusException("Order is already " + status);
        }

        // TODO: throw order executed event
        executions.add(execution);
        updateStatus();
    }

    private void updateStatus() {
        int remainingQuantity = getRemainingQuantity();

        if(remainingQuantity == 0) {
            status = OrderStatus.EXECUTED;
        }
        else if(remainingQuantity == quantity) {
            status = OrderStatus.OPENED;
        }
        else {
            status = OrderStatus.PARTIALLY_EXECUTED;
        }
    }

    public int getExecutedQuantity() {
        int total = 0;

        for(Execution execution : executions) {
            total += execution.getQuantity();
        }

        return total;
    }

    public void update(int quantity, BigDecimal price) throws OrderInvalidStatusException {
        if(status != OrderStatus.OPENED) {
            throw new OrderInvalidStatusException("Order can't be changed");
        }

        setPrice(price);
        setQuantity(quantity);
    }

    public int getRemainingQuantity() {
        return quantity - getExecutedQuantity();
    }

    public String getId() {
        return id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Equity getEquity() {
        return equity;
    }

    public void setEquity(Equity equity) {
        this.equity = equity;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public byte getStatus() {
        return status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public OrderSide getSide() {
        return side;
    }

    public void setSide(OrderSide side) {
        this.side = side;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public List<Execution> getExecutions() {
        return executions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id.equals(order.id) &&
                getEquity().equals(order.getEquity()) &&
                getUserId().equals(order.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, getEquity(), getUserId());
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", equity=" + equity +
                ", side=" + side +
                ", price=" + price +
                ", quantity=" + quantity +
                ", remaining=" + getRemainingQuantity() +
                ", createdTime=" + createdTime +
                ", status=" + OrderStatus.toString(status) +
                ", userId=" + userId +
                '}';
    }
}

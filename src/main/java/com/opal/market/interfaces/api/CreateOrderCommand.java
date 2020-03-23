package com.opal.market.interfaces.api;

import com.opal.market.domain.models.order.OrderSide;

import java.math.BigDecimal;

public class CreateOrderCommand {

    private Long userId;

    private String symbol;

    private BigDecimal price;

    private int quantity;

    private OrderSide side;


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
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

    public OrderSide getSide() {
        return side;
    }

    public void setSide(OrderSide side) {
        this.side = side;
    }
}

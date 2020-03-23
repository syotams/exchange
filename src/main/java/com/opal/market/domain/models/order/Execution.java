package com.opal.market.domain.models.order;

import java.math.BigDecimal;

public class Execution {

    private String sellOrderId;

    private String buyOrderId;

    private int quantity;

    private BigDecimal price;


    public Execution(String sellId, String buyId, int quantity, BigDecimal price) {
        this.sellOrderId = sellId;
        this.buyOrderId = buyId;
        this.quantity = quantity;
        this.price = price;
    }

    public String getSellOrderId() {
        return sellOrderId;
    }

    public String getBuyOrderId() {
        return buyOrderId;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }
}

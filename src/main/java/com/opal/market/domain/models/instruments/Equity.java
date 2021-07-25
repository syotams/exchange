package com.opal.market.domain.models.instruments;

import java.util.Objects;

public class Equity implements Cloneable {

    private String symbol;


    public Equity(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public Equity clone() {
        Equity equity;

        try {
            equity = (Equity) super.clone();
        } catch (CloneNotSupportedException e) {
            equity = new Equity(symbol);
        }

        return equity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Equity equity = (Equity) o;
        return getSymbol().equals(equity.getSymbol());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSymbol());
    }

    @Override
    public String toString() {
        return "Equity{" +
                "symbol='" + symbol + '\'' +
                '}';
    }
}

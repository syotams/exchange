package com.opal.market.domain.models.equity;

import java.util.Objects;

public class Equity {

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

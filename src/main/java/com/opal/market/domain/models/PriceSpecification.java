package com.opal.market.domain.models;

import com.opal.market.domain.shared.AbstractSpecification;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PriceSpecification extends AbstractSpecification<BigDecimal> {

    private BigDecimal buyPrice = BigDecimal.ZERO;


    public void setBuyPrice(BigDecimal buyPrice) {
        this.buyPrice = buyPrice;
    }

    @Override
    public boolean isSatisfiedBy(BigDecimal sellPrice) {
        return buyPrice.compareTo(sellPrice) >= 0;
    }
}

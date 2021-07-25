package com.opal.market.config;

import com.opal.market.domain.models.market.Exchange;
import com.opal.market.domain.service.order.OrdersService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @Value("${equities}")
    private String[] equities;


    @Bean
    public Exchange getExchange(ApplicationContext applicationContext) {
        Exchange exchange = new Exchange(applicationContext.getBean(OrdersService.class));
        exchange.addInstruments(equities);
        return exchange;
    }
}

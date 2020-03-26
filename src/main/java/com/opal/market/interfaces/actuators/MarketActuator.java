package com.opal.market.interfaces.actuators;

import com.opal.market.application.market.IMarketApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Endpoint(id = "market", enableByDefault=true)
public class MarketActuator {

    private IMarketApplicationService marketApplicationService;


    public MarketActuator(@Autowired IMarketApplicationService marketApplicationService) {
        this.marketApplicationService = marketApplicationService;
    }

    @ReadOperation
    public Map<String, String> stats() {
        return marketApplicationService.stats();
    }

}

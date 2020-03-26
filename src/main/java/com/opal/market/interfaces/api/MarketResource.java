package com.opal.market.interfaces.api;


import com.opal.market.application.market.IMarketApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1.0/market")
public class MarketResource {

    private IMarketApplicationService marketApplicationService;


    public MarketResource(@Autowired IMarketApplicationService marketApplicationService) {
        this.marketApplicationService = marketApplicationService;
    }

    @GetMapping(path = {"/stats"})
    public Map<String, String> stats() {
        return marketApplicationService.stats();
    }

}

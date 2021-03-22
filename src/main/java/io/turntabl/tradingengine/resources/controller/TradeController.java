package io.turntabl.tradingengine.resources.controller;

import io.turntabl.tradingengine.resources.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="trade")
public class TradeController {

    @Autowired
    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @PutMapping(path="/{tradeId}/{exchangeId}")
    public void updateTrade(@PathVariable String tradeId, @PathVariable String exchangeId){
        tradeService.updateTrade(tradeId, exchangeId);
    }
}

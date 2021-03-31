package io.turntabl.tradingengine.resources.controller;

import java.util.List;

import io.turntabl.tradingengine.resources.model.Response;
import io.turntabl.tradingengine.resources.model.Trade;
import io.turntabl.tradingengine.resources.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping(path = "/order/{orderId}")
    public List<Trade> getTradesPerOrder(@PathVariable Long orderId) {
        return tradeService.getTradesPerOrder(orderId);
    }

    @DeleteMapping(path = "/cancel-order/{orderId}")
    public Response cancelOrder(@PathVariable("orderId") String orderId){
        return tradeService.cancelOrder(orderId);
    }
}

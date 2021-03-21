package io.turntabl.tradingengine.resources.service;

import io.turntabl.tradingengine.resources.model.Trade;
import io.turntabl.tradingengine.resources.repository.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class TradeService {
    @Autowired
    private final TradeRepository tradeRepository;

    @Autowired
    public TradeService(TradeRepository tradeRepository){
        this.tradeRepository = tradeRepository;
    }

    // Create trade objects
    public void createTrade(Trade trade){
        tradeRepository.save(trade);
    }
}

package io.turntabl.tradingengine.resources.service;

import io.turntabl.tradingengine.resources.model.Trade;
import io.turntabl.tradingengine.resources.repository.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
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

    // Update trade object
    @Transactional
    public void updateTrade(String id, String exchangeId){
        Trade trade = tradeRepository.findById(Long.valueOf(id)).orElse(null);
        if(trade!=null){
            trade.setExchange_order_id(exchangeId);
            tradeRepository.save(trade);
        }else{
            throw new IllegalStateException("trade does not exist");
        }
    }
}

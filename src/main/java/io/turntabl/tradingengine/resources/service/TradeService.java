package io.turntabl.tradingengine.resources.service;

import io.turntabl.tradingengine.resources.model.Orders;
import io.turntabl.tradingengine.resources.model.Trade;
import io.turntabl.tradingengine.resources.repository.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    // Get All Open Trades
    public List<Trade> getAllOpenTrade(){
        return tradeRepository.findAll().stream()
                .filter(trade->trade.getStatus()!=null
                        && trade.getStatus().equals("OPEN")
                        &&trade.getExchange_order_id()!=null)
                .collect(Collectors.toList());

    }

    //Update a trade
    public void changeTradeStatus(Long id, String status){
        Trade trade = tradeRepository.findById(id).orElse(null);
        if(trade!=null){
            trade.setStatus(status);
            tradeRepository.save(trade);
        }else{
            throw new IllegalStateException("trade does not exist");
        }
    }

    // Get All Closed Trades
    public List<Trade> getAllClosedTrade(Orders order){
        return tradeRepository.findAll().stream()
                .filter(trade->trade.getStatus()!=null
                        && trade.getStatus().equals("CLOSE")
                        &&trade.getExchange_order_id()!=null
                        &&trade.getOrders().equals(order))
                .collect(Collectors.toList());

    }
}

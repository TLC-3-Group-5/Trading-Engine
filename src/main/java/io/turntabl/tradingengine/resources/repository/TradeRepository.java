package io.turntabl.tradingengine.resources.repository;

import io.turntabl.tradingengine.resources.model.OwnedStock;
import io.turntabl.tradingengine.resources.model.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TradeRepository extends JpaRepository<Trade, Long> {
}

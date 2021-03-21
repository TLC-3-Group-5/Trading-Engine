package io.turntabl.tradingengine.resources.repository;

import io.turntabl.tradingengine.resources.model.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepository extends JpaRepository<Trade, Long> {
}

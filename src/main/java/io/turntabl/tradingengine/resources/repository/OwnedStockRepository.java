package io.turntabl.tradingengine.resources.repository;

import io.turntabl.tradingengine.resources.model.OwnedStock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnedStockRepository extends JpaRepository<OwnedStock,Long> {
}

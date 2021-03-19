package io.turntabl.tradingengine.resources.repository;

import io.turntabl.tradingengine.resources.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
}

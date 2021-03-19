package io.turntabl.tradingengine.resources.repository;

import io.turntabl.tradingengine.resources.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Orders,Long> {
}

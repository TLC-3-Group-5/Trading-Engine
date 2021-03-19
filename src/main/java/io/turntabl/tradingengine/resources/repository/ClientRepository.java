package io.turntabl.tradingengine.resources.repository;

import io.turntabl.tradingengine.resources.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client,Long> {
    Optional<Client> findClientByEmail(String email);
}

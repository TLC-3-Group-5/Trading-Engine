package io.turntabl.tradingengine.resources.service;

import io.turntabl.tradingengine.resources.repository.OwnedStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OwnedStockService {

    private final OwnedStockRepository ownedStockRepository;

    @Autowired
    public OwnedStockService(OwnedStockRepository ownedStockRepository){
        this.ownedStockRepository = ownedStockRepository;
    }
}

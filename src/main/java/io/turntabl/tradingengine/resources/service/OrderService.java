package io.turntabl.tradingengine.resources.service;

import io.turntabl.tradingengine.resources.model.Orders;
import io.turntabl.tradingengine.resources.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }

    public void createOrders(Orders orders){
        this.orderRepository.save(orders);
    }

    public Orders getOrder(Long id){ return this.orderRepository.getOne(id);}
}

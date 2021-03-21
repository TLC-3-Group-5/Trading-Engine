package io.turntabl.tradingengine.subscriber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.turntabl.tradingengine.resources.model.Orders;
import io.turntabl.tradingengine.resources.model.Trade;
import io.turntabl.tradingengine.resources.repository.OrderRepository;
import io.turntabl.tradingengine.resources.repository.TradeRepository;
import io.turntabl.tradingengine.resources.service.OrderService;
import io.turntabl.tradingengine.resources.service.TradeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
public class Receiver implements MessageListener {
    ObjectMapper objectMapper = new ObjectMapper();
    Logger logger = LoggerFactory.getLogger(Receiver.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private TradeService tradeService;


    public Receiver(OrderService orderService, TradeService tradeService) {
        this.orderService = orderService;
        this.tradeService = tradeService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            Orders request = objectMapper.readValue(message.toString(), Orders.class);
            System.out.println("PortfolioId "+request.getId());
            System.out.println("Product "+request.getProduct());

            Trade trade = new Trade();
            trade.setProduct(request.getProduct());
            trade.setPrice(request.getPrice());
            trade.setQuantity(request.getQuantity());
            trade.setSide(request.getSide());
            trade.setOrders(orderService.getOrder(request.getId()));
            tradeService.createTrade(trade);

            Jedis jedis = new Jedis("localhost");
            jedis.rpush("exchange1", String.valueOf(trade));

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        logger.info("Consumed event {}", message);
    }
}

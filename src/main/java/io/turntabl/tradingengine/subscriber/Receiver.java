package io.turntabl.tradingengine.subscriber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.turntabl.tradingengine.resources.model.Orders;
import io.turntabl.tradingengine.resources.model.Trade;
import io.turntabl.tradingengine.resources.repository.OrderRepository;
import io.turntabl.tradingengine.resources.repository.TradeRepository;
import io.turntabl.tradingengine.resources.service.OrderService;
import io.turntabl.tradingengine.resources.service.TradeService;
import org.hibernate.service.spi.InjectService;
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
    private OrderRepository orderRepository;

    @Autowired
    private TradeRepository tradeRepository;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            Orders request = objectMapper.readValue(message.toString(), Orders.class);
//            System.out.println("PortfolioId "+request.getId());
//            System.out.println("Product "+request.getProduct());

            Trade trade = new Trade();
            trade.setProduct(request.getProduct());
            trade.setPrice(request.getPrice());
            trade.setQuantity(request.getQuantity());
            trade.setSide(request.getSide());
            trade.setExchange("exchange2");
            trade.setOrders(orderRepository.findById(request.getId()).orElse(null));
            tradeRepository.save(trade);
//            System.out.println(trade);
//            objectMapper.writeValueAsString(trade.getOrders());
            Jedis jedis = new Jedis("localhost");
            jedis.rpush("exchange1", objectMapper.writeValueAsString(trade));

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
//        logger.info("Consumed event {}", message);
    }
}

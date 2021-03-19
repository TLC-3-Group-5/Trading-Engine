package io.turntabl.tradingengine.subscriber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.turntabl.tradingengine.dto.OrderRequest;
import io.turntabl.tradingengine.resources.model.Orders;
import io.turntabl.tradingengine.resources.service.OrderService;
import io.turntabl.tradingengine.resources.service.PortfolioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class Receiver implements MessageListener {
    ObjectMapper objectMapper = new ObjectMapper();
    Logger logger = LoggerFactory.getLogger(Receiver.class);


    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            Orders request=objectMapper.readValue(message.toString(), Orders.class);
            System.out.println("PortfolioId "+request.getId());
            System.out.println("Product "+request.getProduct());

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        logger.info("Consumed event {}", message);
    }
}

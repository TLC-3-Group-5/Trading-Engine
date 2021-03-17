package io.turntabl.tradingengine.subscriber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.turntabl.tradingengine.dto.OrderRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class Receiver implements MessageListener {
    ObjectMapper objectMapper = new ObjectMapper();
    Logger logger = LoggerFactory.getLogger(Receiver.class);

    @Override
    public void onMessage(Message message, byte[] bytes) {
        try {
            OrderRequest request=objectMapper.readValue(message.toString(), OrderRequest.class);
            System.out.println("PortfolioId "+request.getPortfolioId());
            System.out.println("Product "+request.getProduct());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        logger.info("Consumed event {}", message);
    }
}

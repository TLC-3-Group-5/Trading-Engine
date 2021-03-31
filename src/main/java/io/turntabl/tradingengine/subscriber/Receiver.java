package io.turntabl.tradingengine.subscriber;

import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.turntabl.tradingengine.resources.model.ExchangeMarketData;
import io.turntabl.tradingengine.resources.model.Orders;
import io.turntabl.tradingengine.resources.model.Trade;
import io.turntabl.tradingengine.resources.repository.OrderRepository;
import io.turntabl.tradingengine.resources.repository.TradeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

@Service
public class Receiver implements MessageListener {
    ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    Logger logger = LoggerFactory.getLogger(Receiver.class);

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private Environment env;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            Orders request = objectMapper.readValue(message.toString(), Orders.class);
            List<Trade> splitOrder = splitOrder(request);

            Jedis jedis = new Jedis(
                Optional.ofNullable(env.getProperty("app.SPRING_REDIS_URI")).orElse("")
            );
            
            for(Trade trade : splitOrder){
                jedis.rpush("exchange1", objectMapper.writeValueAsString(trade));
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public List<Trade> splitOrder(Orders order) throws JsonProcessingException {
        List<Trade> tradeList = new ArrayList<>();

        ExchangeMarketData marketData_1 = objectMapper
                .readValue(restTemplate.getForObject("https://exchange.matraining.com/md/".concat(order.getProduct()), String.class),
                        ExchangeMarketData.class);

        ExchangeMarketData marketData_2 = objectMapper
                .readValue(restTemplate.getForObject("https://exchange2.matraining.com/md/".concat(order.getProduct()), String.class),
                        ExchangeMarketData.class);
//        System.out.println(marketData_1);
//        System.out.println(marketData_2);
//        System.out.println(order);

        if(order.getSide().equals("BUY")){

            if(marketData_1.getASK_PRICE() <= marketData_2.getASK_PRICE()){

                if(marketData_1.getASK_PRICE() != 0){
                    int amountBought = Math.min(order.getQuantity(), marketData_1.getBUY_LIMIT());
                    Trade trade = new Trade();
                    trade.setProduct(order.getProduct());
                    trade.setPrice(Math.min(order.getPrice(),marketData_1.getASK_PRICE()));
                    trade.setQuantity(amountBought);
                    trade.setSide(order.getSide());
                    trade.setExchange("exchange");
                    trade.setStatus("OPEN");
                    trade.setOrders(orderRepository.findById(order.getId()).orElse(null));
                    tradeRepository.save(trade);
                    tradeList.add(trade);

                    int remainder = order.getQuantity() - amountBought;

                    if(remainder>0){
                        Trade trade1 = new Trade();
                        trade.setProduct(order.getProduct());
                        trade.setPrice(Math.min(order.getPrice(), marketData_2.getASK_PRICE()));
                        trade.setQuantity(remainder);
                        trade.setSide(order.getSide());
                        trade.setExchange("exchange2");
                        trade.setStatus("OPEN");
                        trade.setOrders(orderRepository.findById(order.getId()).orElse(null));
                        tradeRepository.save(trade1);
                        tradeList.add(trade1);
                    }

                }else{
                    Trade trade = new Trade();
                    trade.setProduct(order.getProduct());
                    trade.setPrice(Math.min(order.getPrice(), marketData_2.getASK_PRICE()));
                    trade.setQuantity(order.getQuantity());
                    trade.setSide(order.getSide());
                    trade.setExchange("exchange2");
                    trade.setStatus("OPEN");
                    trade.setOrders(orderRepository.findById(order.getId()).orElse(null));
                    tradeRepository.save(trade);
                    tradeList.add(trade);
                }
            }else{

                if(marketData_2.getASK_PRICE()!=0){
                    int amountBought = Math.min(order.getQuantity(), marketData_2.getBUY_LIMIT());
                    Trade trade = new Trade();
                    trade.setProduct(order.getProduct());
                    trade.setPrice(Math.min(order.getPrice(), marketData_2.getASK_PRICE()));
                    trade.setQuantity(amountBought);
                    trade.setSide(order.getSide());
                    trade.setExchange("exchange2");
                    trade.setStatus("OPEN");
                    trade.setOrders(orderRepository.findById(order.getId()).orElse(null));
                    tradeRepository.save(trade);
                    tradeList.add(trade);

                    int remainder = order.getQuantity() - amountBought;

                    if(remainder>0){
                        Trade trade1 = new Trade();
                        trade1.setProduct(order.getProduct());
                        trade1.setPrice(Math.min(order.getPrice(),marketData_1.getASK_PRICE()));
                        trade1.setQuantity(remainder);
                        trade1.setSide(order.getSide());
                        trade1.setExchange("exchange");
                        trade1.setStatus("OPEN");
                        trade1.setOrders(orderRepository.findById(order.getId()).orElse(null));
                        tradeRepository.save(trade1);
                        tradeList.add(trade1);
                    }
                }else{

                    Trade trade1 = new Trade();
                    trade1.setProduct(order.getProduct());
                    trade1.setPrice(Math.min(order.getPrice(), marketData_1.getASK_PRICE()));
                    trade1.setQuantity(order.getQuantity());
                    trade1.setSide(order.getSide());
                    trade1.setExchange("exchange");
                    trade1.setStatus("OPEN");
                    trade1.setOrders(orderRepository.findById(order.getId()).orElse(null));
                    tradeRepository.save(trade1);
                    tradeList.add(trade1);

                }


            }

        }else{
            if(marketData_1.getBID_PRICE() >= marketData_2.getBID_PRICE()){
                int amountSold = Math.min(order.getQuantity(), marketData_1.getSELL_LIMIT());
                Trade trade = new Trade();
                trade.setProduct(order.getProduct());
                trade.setPrice(Math.max(order.getPrice(), marketData_1.getBID_PRICE()));
                trade.setQuantity(amountSold);
                trade.setSide(order.getSide());
                trade.setExchange("exchange");
                trade.setStatus("OPEN");
                trade.setOrders(orderRepository.findById(order.getId()).orElse(null));
                tradeRepository.save(trade);
                tradeList.add(trade);

                int remainder = order.getQuantity() - amountSold;

                if(remainder>0){
                    Trade trade1 = new Trade();
                    trade.setProduct(order.getProduct());
                    trade.setPrice(Math.max(order.getPrice(), marketData_2.getBID_PRICE()));
                    trade.setQuantity(remainder);
                    trade.setSide(order.getSide());
                    trade.setExchange("exchange2");
                    trade.setStatus("OPEN");
                    trade.setOrders(orderRepository.findById(order.getId()).orElse(null));
                    tradeRepository.save(trade1);
                    tradeList.add(trade1);
                }
            }else{

                int amountSold = Math.min(order.getQuantity(), marketData_2.getSELL_LIMIT());
                Trade trade = new Trade();
                trade.setProduct(order.getProduct());
                trade.setPrice(Math.max(order.getPrice(), marketData_2.getBID_PRICE()));
                trade.setQuantity(amountSold);
                trade.setSide(order.getSide());
                trade.setExchange("exchange2");
                trade.setStatus("OPEN");
                trade.setOrders(orderRepository.findById(order.getId()).orElse(null));
                tradeRepository.save(trade);
                tradeList.add(trade);

                int remainder = order.getQuantity() - amountSold;

                if(remainder>0){
                    Trade trade1 = new Trade();
                    trade.setProduct(order.getProduct());
                    trade.setPrice(Math.max(order.getPrice(), marketData_1.getBID_PRICE()));
                    trade.setQuantity(remainder);
                    trade.setSide(order.getSide());
                    trade.setExchange("exchange");
                    trade.setStatus("OPEN");
                    trade.setOrders(orderRepository.findById(order.getId()).orElse(null));
                    tradeRepository.save(trade1);
                    tradeList.add(trade1);
                }
            }
        }

        return tradeList;
    }
}

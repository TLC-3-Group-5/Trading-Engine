package io.turntabl.tradingengine.config;

import io.turntabl.tradingengine.resources.repository.OrderRepository;
import io.turntabl.tradingengine.resources.repository.TradeRepository;
import io.turntabl.tradingengine.resources.service.OrderService;
import io.turntabl.tradingengine.resources.service.TradeService;
import io.turntabl.tradingengine.subscriber.Receiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericToStringSerializer;


@Configuration
public class RedisConfig {

    @Autowired
    Receiver receiver;

//    @Autowired
//    private OrderService orderService;
//
//    @Autowired
//    private TradeService tradeService;
//
//    public RedisConfig(OrderService orderService, TradeService tradeService) {
//        this.orderService = orderService;
//        this.tradeService = tradeService;
//    }

//    @Bean
//    Receiver receiver(OrderService orderService, TradeService tradeService){
//        return new Receiver(orderService, tradeService);
//    }

//    @Bean
//    OrderService orderService(OrderRepository orderRepository){
//        return new OrderService(orderRepository);
//    }
//
//    @Bean
//    TradeService tradeService(TradeRepository tradeRepository){
//        return new TradeService(tradeRepository);
//    }

    @Bean
    public JedisConnectionFactory connectionFactory(){
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName("localhost");
        configuration.setPort(6379);
        return  new JedisConnectionFactory(configuration);
    }


    @Bean
    public RedisTemplate<String, Object> template(){
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory());
        template.setValueSerializer(new GenericToStringSerializer<Object>(Object.class));
        return template;
    }

    @Bean
    public ChannelTopic topic(){
       return new ChannelTopic("orders");
    }

    @Bean
    public MessageListenerAdapter messageListenerAdapter(){
        return new MessageListenerAdapter(receiver);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.addMessageListener(messageListenerAdapter(), topic());
        return container;
    }
}

package io.turntabl.tradingengine.publisher;

import io.turntabl.tradingengine.dto.OrderRequest;
import io.turntabl.tradingengine.resources.model.Orders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Publisher{

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ChannelTopic topic;

    public Publisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic topic){
       this.redisTemplate = redisTemplate;
       this.topic = topic;
    }

    @PostMapping(path="/publish")
    public String publish(@RequestBody Orders orderRequest){
        redisTemplate.convertAndSend(topic.getTopic(), orderRequest.toString());
        return "Event published";
    }

}

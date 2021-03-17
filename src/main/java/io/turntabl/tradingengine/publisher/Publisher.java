package io.turntabl.tradingengine.publisher;

import io.turntabl.tradingengine.dto.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Publisher {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ChannelTopic topic;

    @PostMapping(path = "/publish")
    public String publish(@RequestBody OrderRequest orderRequest){
        redisTemplate.convertAndSend(topic.getTopic(), orderRequest.toString());
        return "Event published";
    }

}

package io.turntabl.tradingengine.resources.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.turntabl.tradingengine.resources.model.Trade;
import io.turntabl.tradingengine.resources.model.TradeExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
@EnableAsync
public class TradeStatusScheduler {

  @Autowired
  private final TradeService tradeService;

  @Autowired
  RestTemplate restTemplate;

  ObjectMapper objectMapper = new ObjectMapper();

  public TradeStatusScheduler(TradeService tradeService) {
    this.tradeService = tradeService;
  }

  @Async
  @Scheduled(fixedDelay = 5000)
  public void scheduleFixedRateTaskAsync() throws InterruptedException {
    // TODO: logic
//    System.out.println("printing from trade scheduler");
    // fetch all trades with status=open and exchange_id != null
//    System.out.println(tradeService.getAllOpenTrade());
    List<Trade> OpenTradeList = tradeService.getAllOpenTrade();
    // for each of such trade, get exchange status (call service)
    List<String> status = OpenTradeList.stream()
            .map(trade ->restTemplate.getForObject("http://localhost:8084/get-order-status/"
                    .concat(trade.getExchange_order_id())
                    .concat("/").concat(trade.getExchange()), String.class)).collect(Collectors.toList());
    System.out.println(status);
//    List<TradeExecution> tradeExecutions = status.stream().map(st-> {
//      try {
//        return objectMapper.readValue(st, TradeExecution.class);
//      } catch (JsonProcessingException e) {
//        e.printStackTrace();
//      }
//      return null;
//    }).collect(Collectors.toList());

    // update trade accordingly
    // update client order if necessary
    // update portfolio if necessary
    // update user if necessary
  }
  
}

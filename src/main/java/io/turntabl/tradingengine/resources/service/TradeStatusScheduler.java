package io.turntabl.tradingengine.resources.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
@EnableAsync
public class TradeStatusScheduler {

  @Autowired
  private final TradeService tradeService;

  @Autowired
  RestTemplate restTemplate;

  ObjectMapper objectMapper = new ObjectMapper()
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


  public TradeStatusScheduler(TradeService tradeService) {
    this.tradeService = tradeService;
  }

  @Async
  @Scheduled(fixedDelay = 5000)
  public void scheduleFixedRateTaskAsync() throws InterruptedException, JsonProcessingException {

    // fetch all trades with status=open and exchange_id != null
    // for each of such trade, get exchange status (call service)
    // update trade accordingly
    // update client order if necessary
    // update portfolio if necessary
    // update user if necessary

    List<Trade> OpenTradeList = tradeService.getAllOpenTrade();

    for(Trade trade: OpenTradeList){
      String data = restTemplate.getForObject("http://localhost:8084/get-order-status/"
              .concat(trade.getExchange_order_id())
              .concat("/").concat(trade.getExchange()), String.class);
      TradeExecution execution = objectMapper.readValue(data, TradeExecution.class);

      if(execution.getQuantity()==execution.getCumulativeQuantity()){
        tradeService.changeTradeStatus(trade.getId(), "CLOSE");
        List<Trade> trades =
                (List<Trade>) objectMapper.readValue(restTemplate.getForObject("http://localhost:8082/trade/"
                        .concat(String.valueOf(trade.getOrders().getId())), String.class), Trade.class);

        if(trades.isEmpty()){
          Map<String, Long> variables = new HashMap<>();
          variables.put("orderId", trade.getOrders().getId());
          restTemplate.put("http://localhost:8082/update-order-status/{orderId}", "CLOSE", variables);

          if(trade.getOrders().getStatus().equals("BUY")){

            List<Trade> closedTrades = tradeService.getAllClosedTrade(trade.getOrders());
            Map<String, Long> parameters = new HashMap<>();
            parameters.put("portfolioId", trade.getOrders().getPortfolio().getId());
            restTemplate.put("http://localhost:8081/portfolio/add-stock-to-portfolio/{portfolioId}", closedTrades, parameters);

          }else{

            List<Trade> closedTrades = tradeService.getAllClosedTrade(trade.getOrders());
            Double valueOfTrades = closedTrades.stream()
                    .mapToDouble(trade1 -> trade1.getPrice()*trade1.getQuantity()).sum();
            Map<String, Long> parameters = new HashMap<>();
            parameters.put("portfolioId", trade.getOrders().getPortfolio().getId());
            restTemplate.put("http://localhost:8081/portfolio/update-client-balance-after-sale/{portfolioId}", valueOfTrades, parameters);

          }
        }else{
          Map<String, Long> variables = new HashMap<>();
          variables.put("orderId", trade.getOrders().getId());
          restTemplate.put("http://localhost:8082/update-order-status/{orderId}", "PARTIAL", variables);
        }

      }

    }
  }
  
}

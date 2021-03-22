package io.turntabl.tradingengine.resources.service;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@EnableAsync
public class TradeStatusScheduler {
  
  @Async
  @Scheduled(fixedDelay = 5000)
  public void scheduleFixedRateTaskAsync() throws InterruptedException {
    // TODO: logic
    
    // fetch all trades with status=open
    // for each of such trade, get exhange status (call service)
    // update trade acordingly
    // update client order if necessary
    // update portfolio if necessary
    // update user if necessary
  }
  
}

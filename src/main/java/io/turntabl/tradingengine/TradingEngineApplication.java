package io.turntabl.tradingengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class TradingEngineApplication {

	@Bean
	private static RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(TradingEngineApplication.class, args);
	}

}

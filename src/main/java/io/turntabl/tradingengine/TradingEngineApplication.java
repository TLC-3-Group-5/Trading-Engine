package io.turntabl.tradingengine;

import io.turntabl.tradingengine.OrderBookRequest.OrderBookRequest;
import io.turntabl.tradingengine.PendingOrder.PendingOrder;
import io.turntabl.tradingengine.Utility.Utility;
import io.turntabl.tradingengine.ValidateOrder.ValidateOrder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class TradingEngineApplication {

	public static void main(String[] args) {
		ValidateOrder validateOrder = new ValidateOrder("A201", "Apl", "2.0", "200", "Bye", "on", "2018/15/25","201548");
		String validatedOrderString = Utility.convertToString(validateOrder);

		new Jedis().lpush("makeorder", validatedOrderString);

		System.out.println(validatedOrderString);
		SpringApplication.run(TradingEngineApplication.class, args);

		//	MAKE ORDERBOOK REQUEST
		new Thread(new Runnable() {
			Jedis jedis = new Jedis();

			@Override
			public void run() {
				while (true){
					String data = jedis.rpop("makeorder");
					if(data == null) continue;

					ValidateOrder validatedOrder = Utility.convertToObject(data,ValidateOrder.class);
					jedis.set(validatedOrder.getId(),data);
					jedis.lpush("monitorqueue", validatedOrder.getId());
					OrderBookRequest orderBookRequest = new OrderBookRequest(
							validatedOrder.getId(),
							validatedOrder.getProduct(),
							validatedOrder.getSide()
					);
					String requestString = Utility.convertToString(orderBookRequest);
					jedis.lpush("exchange1-orderrequest",requestString);
					jedis.lpush("exchange2-orderrequest",requestString);
				}
			}
		}).start();

		//	MONITOR QUEUE
		new Thread(new Runnable() {
			Jedis jedis = new Jedis();

			@Override
			public void run() {
				while (true){
					String data = jedis.rpop("monitorqueue");
					if(data == null) continue;
					if(jedis.llen(data+"orderbook") == 2){

						PendingOrder[] pendingOrderList1 = Utility.convertToObject(jedis.rpop(data+"orderbook"),PendingOrder[].class);
						PendingOrder[] pendingOrderList2 = Utility.convertToObject(jedis.rpop(data+"orderbook"),PendingOrder[].class);

						List<PendingOrder> pendingOrderList = new ArrayList<>();

						pendingOrderList.addAll(Arrays.asList(pendingOrderList1));
						pendingOrderList.addAll(Arrays.asList(pendingOrderList2));

						ValidateOrder validatedOrder = Utility.convertToObject( jedis.get(data), ValidateOrder.class);

						jedis.lpush("makeorder"+"exchange1");

					}else{
						jedis.lpush("monitorqueue",data);
					}
				}
			}
		}).start();

	}

}

package io.turntabl.tradingengine.resources.service;

import io.turntabl.tradingengine.resources.model.Orders;
import io.turntabl.tradingengine.resources.model.Response;
import io.turntabl.tradingengine.resources.model.Trade;
import io.turntabl.tradingengine.resources.repository.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TradeService {
    @Autowired
    private final TradeRepository tradeRepository;

    @Autowired
    RestTemplate restTemplate;

    @Value("${app.ovs_url}")
    private String ovsUrl;

    @Value("${app.exchange_connectivity_url}")
    private String exchangeUrl;

    @Autowired
    public TradeService(TradeRepository tradeRepository){
        this.tradeRepository = tradeRepository;
    }

    // Create trade objects
    public void createTrade(Trade trade){
        tradeRepository.save(trade);
    }

    // Update trade object
    @Transactional
    public void updateTrade(String id, String exchangeId){
        Trade trade = tradeRepository.findById(Long.valueOf(id)).orElse(null);
        if(trade!=null){
            trade.setExchange_order_id(exchangeId);
            tradeRepository.save(trade);
        }else{
            throw new IllegalStateException("trade does not exist");
        }
    }

    // Get All Open Trades
    public List<Trade> getAllOpenTrade(){
        return tradeRepository.findAll().stream()
                .filter(trade->trade.getStatus()!=null
                        && trade.getStatus().equals("OPEN")
                        &&trade.getExchange_order_id()!=null)
                .collect(Collectors.toList());

    }

    //Update a trade
    public void changeTradeStatus(Long id, String status){
        Trade trade = tradeRepository.findById(id).orElse(null);
        if(trade!=null){
            trade.setStatus(status);
            tradeRepository.save(trade);
        }else{
            throw new IllegalStateException("trade does not exist");
        }
    }

    // Get All Closed Trades
    public List<Trade> getAllClosedTrade(Orders order){
        return tradeRepository.findAll().stream()
                .filter(trade->trade.getStatus()!=null
                        && trade.getStatus().equals("CLOSE")
                        &&trade.getExchange_order_id()!=null
                        &&trade.getOrders().equals(order))
                .collect(Collectors.toList());

    }

    public List<Trade> getTradesPerOrder(Long orderId) {
        return tradeRepository.findAll().stream()
            .filter((Trade t) -> t.getOrders().getId().equals(orderId))
            .collect(Collectors.toList());
    }


    public Response cancelOrder(String orderId){
        Response response = new Response();

        // Cancelling OPEN order
        Map<String, Long> variables = new HashMap<>();
        variables.put("orderId", Long.valueOf(orderId));
        restTemplate.put(
                ovsUrl.concat("/update-order-status/{orderId}"),
                "CANCELLED",
                variables
        );

        // Reimbursing the Client Balance with the value of the Cancelled Order
//        Map<String, Long> parameter = new HashMap<>();
//        variables.put("orderId", Long.valueOf(orderId) );
//        restTemplate.put(
//                ovsUrl.concat("/update-balance-of-cancelled-order/{orderId}"), parameter);
        HttpEntity<?> data = new HttpEntity<>("");
        String url = ovsUrl.concat("/update-balance-of-cancelled-order/").concat(orderId);
        restTemplate.exchange(url, HttpMethod.PUT, data, Void.class);

        // Cancelling all OPEN trades on the exchange
        getTradesPerOrder(Long.valueOf(orderId))
                .stream()
                .filter(trade -> trade.getStatus().equals("OPEN"))
                .forEach(trade -> {
                    HttpEntity<?> request = new HttpEntity<>("");
                    restTemplate.exchange(exchangeUrl, HttpMethod.DELETE, request, Boolean.class);
                    trade.setStatus("CANCELLED");
                    tradeRepository.save(trade);
                });

        response.setCode(204);
        response.setStatus("Order cancelled successfully");
        return response;
    }

}

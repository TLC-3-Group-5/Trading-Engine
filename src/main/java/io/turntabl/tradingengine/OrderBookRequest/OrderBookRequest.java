package io.turntabl.tradingengine.OrderBookRequest;

public class OrderBookRequest {
        private String id;
        private String product;
        private String side;

    public OrderBookRequest(String id, String product, String side) {
        this.id = id;
        this.product = product;
        this.side = side;
    }

    public OrderBookRequest(){
    }
}

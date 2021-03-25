package io.turntabl.tradingengine.resources.model;

import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "product", "quantity", "price", "side", "executions", "cumulatitiveQuantity" })
public class TradeExecution {

    @JsonProperty("product")
    private String product;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("price")
    private Double price;

    @JsonProperty("side")
    private String side;

    @JsonProperty("executions")
    private List<Execution> executions=null;

    @JsonProperty("cumulativeQuantity")
    private int cumulativeQuantity;


    public TradeExecution() {
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public int getCumulativeQuantity() {
        return cumulativeQuantity;
    }

    public void setCumulativeQuantity(int cumulativeQuantity) {
        this.cumulativeQuantity = cumulativeQuantity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public List<Execution> getExecutions() {
        return executions;
    }

    public void setExecutions(List<Execution> executions) {
        this.executions = executions;
    }

    @Override
    public String toString() {
        return "TradeExecution{" +
                "product='" + product + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", side='" + side + '\'' +
                ", executions=" + executions +
                ", cumulativeQuantity=" + cumulativeQuantity +
                '}';
    }
}

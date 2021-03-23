package io.turntabl.tradingengine.resources.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;

public class Execution {
    @Id
    @SequenceGenerator(
            name= "client_sequence",
            sequenceName = "client_sequence",
            allocationSize = 1
    )

    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "client_sequence"
    )
    @Column(
            nullable=false,
            updatable = false
    )
    @JsonIgnore
    private Long id;

    private Double price;

    private Integer quantity;

    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name="tradeexecution_id")
    @JsonBackReference
    private TradeExecution tradeExecution;

    public Execution() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public TradeExecution getTradeExecution() {
        return tradeExecution;
    }

    public void setTradeExecution(TradeExecution tradeExecution) {
        this.tradeExecution = tradeExecution;
    }
}

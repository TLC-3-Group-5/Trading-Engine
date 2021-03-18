package io.turntabl.tradingengine.ValidateOrder;

public class ValidateOrder {
    private String id ;
    private String product;
    private String price;
    private String quantity;
    private String side;
    private String status;
    private String update_at;
    private String created_at;


    public ValidateOrder(){

    }

    public ValidateOrder(String id, String product, String price, String quantity, String side, String status, String update_at, String created_at) {
        this.id = id;
        this.product = product;
        this.price = price;
        this.quantity = quantity;
        this.side = side;
        this.status = status;
        this.update_at = update_at;
        this.created_at = created_at;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

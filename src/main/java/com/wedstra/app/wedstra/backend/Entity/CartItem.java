package com.wedstra.app.wedstra.backend.Entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "cart_items")
public class CartItem {

    @Id
    private String id;

    private String serviceId; // Vendor service ID
    private String serviceName;
    private double price;
    private int quantity;

    public CartItem() {}

    public CartItem(String serviceId, String serviceName, double price, int quantity) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.price = price;
        this.quantity = quantity;
    }


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}

package com.cniao5.cniao5shop.bean;

import java.io.Serializable;

public class OrderItem implements Serializable{

    private Long id;
    private Float amount;
    private Wares wares;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Wares getWares() {
        return wares;
    }

    public void setWares(Wares wares) {
        this.wares = wares;
    }
}

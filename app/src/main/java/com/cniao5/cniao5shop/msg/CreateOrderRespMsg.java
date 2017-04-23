package com.cniao5.cniao5shop.msg;

public class CreateOrderRespMsg extends BaseResMsg{

    private OrderRespMsg data;

    public OrderRespMsg getData() {
        return data;
    }

    public void setData(OrderRespMsg data) {
        this.data = data;
    }
}

package com.ohgiraffers.orderservice.aggregate;

import lombok.Data;

import java.util.List;

@Data
public class Order {
    private int orderCode;
    private int userId;
    private String orderDate;
    private String orderTime;
    private int totalOrderPrice;

    private List<OrderMenu> orderMenus;
}

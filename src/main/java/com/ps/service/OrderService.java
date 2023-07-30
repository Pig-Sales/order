package com.ps.service;

import com.ps.pojo.*;
import com.ps.pojo.Order;
import java.util.List;

public interface OrderService {


    void getOrderByConditions(User username, Goods goods_name, Goods state);
    Order getOrderById(String orderId);
    long howManyAppeal(String openId);
    Order createNewOrder(Order order);
    void updateOldOrder(Order order);
    List<Order> getOrderByConditions(GetOrderByConditions getOrderByConditions);
    List<Tip_Content> getTipContent(String auth);
    List<Button_Content> getButtonContent(String auth);



}

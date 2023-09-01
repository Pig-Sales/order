package com.ps.service;

import com.ps.pojo.*;
import com.ps.pojo.Order;
import io.jsonwebtoken.Claims;

import java.util.List;

public interface OrderService {


    Order getOrderById(String orderId);
    long howManyAppeal(String openId);
    Order createNewOrder(Order order);
    Order updateOldOrder(Order order, Claims claims);
    List<Order> getOrderByConditions(GetOrderByConditions getOrderByConditions);
    List<Tip_Content> getTipContent(String auth);
    List<Button_Content> getButtonContent(String auth);
    List <Order> getOrderByGoodsID(Order order);
}

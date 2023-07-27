package com.ps.service;

import com.ps.pojo.*;

import java.util.List;

public interface OrderService {

    Order getOrderById(String order_id);

    void getOrderByConditions(User username, Goods goods_name, Goods state);
    Order getorderById(String goodsId);
    Order howManyAppeal(String openId);

    void createNewOrder(Order order);

    Order alterOrder();

    List<Tip_Content> getTipContent(String auth);
    List<Button_Content> getButtonContent(String auth);



}

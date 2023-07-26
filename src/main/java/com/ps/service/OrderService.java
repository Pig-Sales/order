package com.ps.service;

import com.ps.pojo.Goods;
import com.ps.pojo.Tip_Content;
import com.ps.pojo.User;
import com.ps.pojo.Order;

import java.util.List;

public interface OrderService {

    Order getOrderById(String order_id);

    void getOrderByConditions(User username, Goods goods_name, Goods state);

    Order howManyAppeal(String openId);

    Order createNewOrder();

    Order alterOrder();

    List<Tip_Content> getTipContent(String auth);



}

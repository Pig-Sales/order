package com.ps.service.impl;


import com.ps.pojo.Goods;
import com.ps.pojo.Order;
import com.ps.pojo.Tip_Content;
import com.ps.pojo.User;
import com.ps.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public Order getOrderById(String order_id) {
        return null;
    }

    @Override
    public void getOrderByConditions(User username, Goods goods_name, Goods state) {

    }

    @Override
    public Order howManyAppeal(String openId) {
        return null;
    }

    @Override
    public Order createNewOrder() {
        return null;
    }

    @Override
    public Order alterOrder() {
        return null;
    }

    @Override
    public List<Tip_Content> getTipContent(String auth) {
        Query query = new Query(Criteria.where("user_auth").is(auth));
        return mongoTemplate.find(query, Tip_Content.class, "tip_content");
    }
}

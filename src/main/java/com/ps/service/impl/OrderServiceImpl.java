package com.ps.service.impl;


import com.ps.pojo.*;
import com.ps.service.OrderService;
import org.bson.types.ObjectId;
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


//    @Override
//    public Order getOrderById(String order_id) {
//        return null;
//    }

    @Override
    public Order getOrderById(String orderId) {
        Query query = new Query(Criteria.where("order_id").is(orderId));
        return mongoTemplate.findOne(query, Order.class, "order");
    }

    @Override
    public void getOrderByConditions(User username, Goods goods_name, Goods state) {

    }
    @Override
    public Order howManyAppeal(String openId) {
        return null;
    }

    @Override
    public void createNewOrder(Order order) {
        order.setOrder_id((new ObjectId()).toString());
        order.setCreate_time(LocalDateTime.now().toString());
        order.setUpdate_time(LocalDateTime.now().toString());
        mongoTemplate.save(order,"order");
    }

    @Override
    public void updateOldOrder(Order order) {
        order.setUpdate_time(LocalDateTime.now().toString());
        Query query = Query.query(Criteria.where("order_id").is(order.getOrder_id()));
        Update update=new Update();
        if(order.getState()!=null){
            update.set("state",order.getState());
        }
        if(order.getOrder_number()!=null){
            update.set("order_number",order.getOrder_number());
        }
        if(order.getOrder_price()!=null) {
            update.set("order_price",order.getOrder_price());
        }
        if(order.getDeposit_time()!=null){
            update.set("deposit_time",order.getDeposit_time());
        }
        if(order.getComplete_time()!=null){
            update.set("complete_time",order.getComplete_time());
        }
        if(order.getQuarantine_state()!=null){
            update.set("quarantine_state",order.getQuarantine_state());
        }
        if(order.getActual_weight()!=null){
            update.set("actual_weight",order.getActual_weight());
        }
        if(order.getActual_total_price()!=null){
            update.set("actual_total_price",order.getActual_total_price());
        }
        if(order.getQuarantine_image()!=null) {
            update.set("buyer_id",order.getBuyer_id());
        }
        if(order.getQuarantine_ask_time()!=null) {
            update.set("quarantine_ask_time",order.getQuarantine_ask_time());
        }
        if(order.getQuarantine_complete_time()!=null) {
            update.set("quarantine_complete_time",order.getQuarantine_complete_time());
        }
        if(order.getGoods_belong()!=null) {
            update.set("goods_belong",order.getGoods_belong());
        }

        if(order.getDeposit_belong()!=null) {
            update.set("deposite_belong",order.getDeposit_belong());
        }

        update.set("update_time",order.getUpdate_time());
        mongoTemplate.updateFirst(query,update,"order");
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
    public List<Button_Content> getButtonContent(String auth) {
        Query query = new Query(Criteria.where("user_auth").is(auth));
        return mongoTemplate.find(query, Button_Content.class, "button_content");
    }
}

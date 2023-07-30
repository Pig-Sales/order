package com.ps.service.impl;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ps.client.UserClient;
import com.ps.client.GoodsClient;
import com.ps.pojo.*;
import com.ps.service.OrderService;
import com.sun.net.httpserver.Authenticator;
import io.jsonwebtoken.Claims;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserClient userClient;
    @Autowired
    private  GoodsClient goodsClient;

    @Override
    public Order getOrderById(String orderId) {
        Query query = new Query(Criteria.where("order_id").is(orderId));
        return mongoTemplate.findOne(query, Order.class, "order");

    }
    @Override
    public long howManyAppeal(String openId) {
        Query query = new Query(Criteria.where("state").is("申诉中"));
        return mongoTemplate.count(query, Order.class);
    }

    @Override
    public Order createNewOrder(Order order) {
        order.setOrder_id((new ObjectId()).toString());
        order.setState("待询价");
        order.setBuyer_confirm(0);
        order.setSeller_confirm(0);
        order.setQuarantine_state("未检疫");
        order.setCreate_time(LocalDateTime.now().toString());
        order.setUpdate_time(LocalDateTime.now().toString());
        mongoTemplate.save(order,"order");
        return mongoTemplate.save(order,"order");
    }

    @Override
    public Order updateOldOrder(Order order, Claims claims) {
        order.setUpdate_time(LocalDateTime.now().toString());
        Query query = Query.query(Criteria.where("order_id").is(order.getOrder_id()));
        Update update=new Update();
        if(order.getState()!=null){
            List<String> order_states1 = Arrays.asList("待询价", "待预付", "待交易", "已完成");
            List<String> order_states2 = Arrays.asList("待询价", "待预付", "已取消");
            List<String> order_states3 = Arrays.asList("待询价", "待预付", "待交易", "申诉中", "已取消");
            List<String> order_states4 = Arrays.asList("待询价", "已取消");
            Order order1 = mongoTemplate.findOne(query, Order.class, "order");
            assert order1 != null;
            if(!Objects.equals(order.getState(), "待询价")) {
                if (!(order_states1.indexOf(order.getState()) != order_states1.indexOf(order1.getState()) + 1 &&
                        order_states2.indexOf(order.getState()) != order_states2.indexOf(order1.getState()) + 1 &&
                        order_states3.indexOf(order.getState()) != order_states3.indexOf(order1.getState()) + 1 &&
                        order_states4.indexOf(order.getState()) != order_states4.indexOf(order1.getState()) + 1)

                ){
                    if(!Objects.equals(order.getState(), "已完成")){
                        if(Objects.equals(order.getState(), "待交易")){
                            update.set("deposit_time",LocalDateTime.now());
                        }
                        update.set("state", order.getState());
                    }
                    else {
                        if(order1.getBuyer_confirm()==1&&order1.getSeller_confirm()==1){
                            update.set("state", order.getState());
                            update.set("complete_time",LocalDateTime.now());
                        }
                        else {
                            if(Objects.equals((String) claims.get("user_auth"), "buyer")){
                                update.set("buyer_confirm",1);
                            }
                            else {
                                update.set("seller_confirm",1);
                            }
                        }
                    }
                }
            }
        }

        if(order.getOrder_number()!=null){
            Order order1 = mongoTemplate.findOne(query, Order.class, "order");

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

        return order;
    }

    @Override
    public List<Order> getOrderByConditions(GetOrderByConditions getOrderByConditions) {

        Pageable pageable;
        pageable = PageRequest.of(getOrderByConditions.getPage_num()-1, getOrderByConditions.getPage_size());
        //and or 查询
        Criteria criteria = new Criteria();
        if (getOrderByConditions.getInput_condition()!= null) {
            if(getOrderByConditions.getState() != null){
                List<String> list = new ArrayList<>();
                List<User> userList = new ObjectMapper().convertValue(
                        userClient.getUseridByName(getOrderByConditions.getInput_condition()).getData(),
                        new TypeReference<List<User>>() {
                        }
                );
                userList.forEach(user -> list.add(user.getUser_id()));
                criteria = new Criteria().andOperator(
                        Criteria.where("state").is(getOrderByConditions.getState()),
                        new Criteria().orOperator(
                                Criteria.where("seller_id").in(list),
                                Criteria.where("buyer_id").in(list),
                                Criteria.where("goods_name").regex(getOrderByConditions.getInput_condition())
                        )
                );
            }
            else{
                List<String> list = new ArrayList<>();
                List<User> userList = new ObjectMapper().convertValue(
                        userClient.getUseridByName(getOrderByConditions.getInput_condition()).getData(),
                        new TypeReference<List<User>>() {
                        }
                );
                userList.forEach(user -> list.add(user.getUser_id()));
                criteria = new Criteria().orOperator(
                                Criteria.where("seller_id").in(list),
                                Criteria.where("buyer_id").in(list),
                                Criteria.where("goods_name").regex(getOrderByConditions.getInput_condition())
                );
            }
        }
        else {
            if(getOrderByConditions.getState() != null){
                criteria = new Criteria().where("state").is(getOrderByConditions.getState());
            }
            else{
                Query query = new Query().with(pageable);
                return mongoTemplate.find(query, Order.class,"order");
            }
        }
        Query query = Query.query(criteria).with(pageable);
        return mongoTemplate.find(query, Order.class,"order");

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

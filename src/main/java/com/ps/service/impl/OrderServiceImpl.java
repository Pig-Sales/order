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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
    public Order createNewOrder(Order order) {  //买方求购即为创建订单
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        DateTimeFormatter dateFormatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter2 = DateTimeFormatter.ofPattern("HH时mm分ss秒");
        String formatTime1 = dateFormatter1.format(date);
        String formatTime2 = timeFormatter2.format(time);
        order.setOrder_id((new ObjectId()).toString());
        order.setState("待询价");
        order.setBuyer_confirm(0);
        //order.setSeller_confirm(0);
        order.setQuarantine_state("未检疫");
        order.setOrder_number(0);
        order.setActual_weight(null);
        order.setActual_total_price(null);
        order.setCreate_time(formatTime1+" "+formatTime2);
        order.setUpdate_time(formatTime1+" "+formatTime2);
        return mongoTemplate.save(order,"order");
    }

    @Override
    public Order updateOldOrder(Order order, Claims claims) {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        DateTimeFormatter dateFormatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter2 = DateTimeFormatter.ofPattern("HH时mm分ss秒");
        String formatTime1 = dateFormatter1.format(date);
        String formatTime2 = timeFormatter2.format(time);
        System.out.println(order.getOrder_number());
        System.out.println(order.getOrder_price());
//        order.setQuarantine_state(order.getQuarantine_state());
//        order.setQuarantine_ask_time(order.getQuarantine_ask_time());
//        order.setQuarantine_complete_time(order.getQuarantine_complete_time());
        Query query = Query.query(Criteria.where("order_id").is(order.getOrder_id()));
        Update update=new Update();
        if(order.getActual_weight() != null){
            if(order.getOrder_price() != null){
                update.set("actual_weight",order.getActual_weight());
                update.set("order_price",order.getOrder_price());
                update.set("actual_total_price",order.getActual_weight()*order.getOrder_price());
            }
            else{
                Order order1 = mongoTemplate.findOne(query, Order.class, "order");
                update.set("actual_weight",order.getActual_weight());
                update.set("actual_total_price",order.getActual_weight()*order1.getOrder_price());
            }
        }
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
                        if(Objects.equals(order.getState(), "待预付")){
                            System.out.println(order.getOrder_number());
                            Order order2 = mongoTemplate.findOne(query, Order.class, "order");
                            update.set("order_number",order.getOrder_number());
                            update.set("order_price",order.getOrder_price());
                            Goods goods = new Goods() ;
                            goods.setGoods_id(order2.getGoods_id());
                            goods.setGoods_number(-order.getOrder_number());
                            goodsClient.updateGoodsNumber(goods);
                        }
                        if(Objects.equals(order.getState(), "待交易")){
                            update.set("deposit_time",formatTime1+" "+formatTime2);
                        }
                        update.set("state", order.getState());
                        System.out.println( order.getState());
                    }
                    else {
                        if(order1.getBuyer_confirm()==1){
                            update.set("state", order.getState());
                            update.set("complete_time",formatTime1+" "+formatTime2);
                        }
                    }
                }
            }
        }

        if(Objects.equals(order.getState(), "已取消")){
            Order order1 = mongoTemplate.findOne(query, Order.class, "order");
            Goods goods = new Goods() ;
            update.set("order_number",order.getOrder_number());
            update.set("order_price",order.getOrder_price());
            goods.setGoods_id(order1.getGoods_id());
            goods.setGoods_number(order1.getOrder_number());
            goodsClient.updateGoodsNumber(goods);
            }
        if(order.getGoods_belong()!=null) {
            order.setGoods_belong(order.getGoods_belong());
        }

        if(order.getDeposit_belong()!=null) {
            order.setDeposit_belong(order.getDeposit_belong());
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
                List<String> list= new ArrayList<>();
                User user1 = new User();
                user1.setUsername(getOrderByConditions.getInput_condition());
                List<User> userList =new ObjectMapper().convertValue(
                        userClient.getUseridByName(user1).getData(),
                        new TypeReference<List<User>>(){}
                );
                System.out.println(getOrderByConditions.getInput_condition());
               System.out.println(userList);
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
                List<String> list= new ArrayList<>();
                User user1 = new User();
                user1.setUsername(getOrderByConditions.getInput_condition());
                List<User> userList =new ObjectMapper().convertValue(
                        userClient.getUseridByName(user1).getData(),
                        new TypeReference<List<User>>(){}
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

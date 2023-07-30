package com.ps.controller;


import com.ps.pojo.*;
import com.ps.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import com.ps.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class OrderController {
    @Autowired

    public OrderService orderService;
    @Value("${jwt.signKey}")
    private String signKey;

    @PostMapping("/order/getOrderById")
    public Result getOrderById(@RequestBody Order order,@RequestHeader String Authorization){
        return Result.success(orderService.getOrderById(order.getOrder_id()));
    }

    @PostMapping("/order/getOrderByConditions")
    public Result getOrderByConditions(@RequestBody GetOrderByConditions getOrderByConditions, @RequestHeader String Authorization){
        Claims claims = JwtUtils.parseJWT(Authorization,signKey);
        return Result.success(orderService.getOrderByConditions(getOrderByConditions));
    }

    @PostMapping("/order/howManyAppeal")
    public Result howManyAppeal(@RequestHeader String Authorization){
        Claims claims = JwtUtils.parseJWT(Authorization,signKey);
        String openId = (String) claims.get("openId");
        Map map = new HashMap<>();
        map.put("number",orderService.howManyAppeal(openId));
        return Result.success(map);
    }
    @PostMapping("/order/createNewOrder")
    public Result createNewOrder(@RequestHeader String Authorization, @RequestBody Order order){
        Claims claims = JwtUtils.parseJWT(Authorization,signKey);
        String auth = (String) claims.get("user_auth");
        if( !"buyer".equals(auth)){
            System.out.println("buyer".equals(auth));
            return Result.error("没有创建订单权限");
        }
        else{
            orderService.createNewOrder(order);
            return Result.success(orderService.createNewOrder(order));
        }
    }

    @PostMapping("/order/alterOrder")
    public Result alterOrder(@RequestBody Order order , @RequestHeader String Authorization){
        Claims claims = JwtUtils.parseJWT(Authorization,signKey);
        orderService.updateOldOrder(order);
        return Result.success();
    }
    @PostMapping("/order/getButtonContent")
    public Result getButtonContent(@RequestHeader String Authorization) {
        Claims claims = JwtUtils.parseJWT(Authorization, signKey);
        String auth = (String) claims.get("user_auth");
        return Result.success(orderService.getButtonContent(auth));
    }
    @PostMapping("/order/getTipContent")
    public Result getTipContent(@RequestHeader String Authorization) {
        Claims claims = JwtUtils.parseJWT(Authorization, signKey);
        String auth = (String) claims.get("user_auth");
        return Result.success(orderService.getTipContent(auth));
    }

}

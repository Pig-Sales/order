package com.ps.controller;


import com.ps.pojo.*;
import com.ps.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import com.ps.service.OrderService;
import org.aspectj.weaver.ast.Or;
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
            //orderService.createNewOrder(order);
            return Result.success(orderService.createNewOrder(order));
        }
    }

    @PostMapping("/order/alterOrder")
    public Result alterOrder(@RequestBody Order order , @RequestHeader String Authorization){
        Claims claims = JwtUtils.parseJWT(Authorization,signKey);
        return Result.success(orderService.updateOldOrder(order,claims));
    }

//    @PostMapping("/order/updateOrderByPlatform")
//    public Result updateOrderByPlatform(@RequestBody Order order , @RequestHeader String Authorization){
//        Claims claims = JwtUtils.parseJWT(Authorization,signKey);
//        String auth = (String) claims.get("user_auth");
//        if (auth.equals("seller") || auth.equals("buyer")){
//            System.out.println(auth);
//            return Result.error("没有修改价格权限！");
//        }
//        return Result.success(orderService.updateOldOrder(order,claims));
//    }

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

    @PostMapping("/order/getOrderByGoodsID")
    public Result getOrderByGoodsID(@RequestBody Order order , @RequestHeader String Authorization){
        Claims claims =JwtUtils.parseJWT(Authorization , signKey);
        String auth = (String) claims.get("user_auth");
        if ("seller".equals(auth))
        {
            return Result.error("没有查看订单列表权限！");//养殖户不能查询订单列表
        }

        return Result.success(orderService.getOrderByGoodsID(order));
    }
}
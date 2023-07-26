package com.ps.controller;


import com.ps.pojo.*;
import com.ps.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import com.ps.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;


@RestController
public class OrderController {
    @Autowired
    public OrderService orderService;
    @Value("${jwt.signKey}")
    private String signKey;

    @PostMapping("/order/getOrderById")
    public Result getOrderById(@RequestBody Order order_id,@RequestHeader String Authorization){

        Claims claims = JwtUtils.parseJWT(Authorization,signKey);
        String openId = (String) claims.get("openId");
        return Result.success();
    }

    @PostMapping("/order/getOrderByConditions")
    public Result getOrderByConditions(@RequestBody Order order, @RequestHeader String Authorization){
        Claims claims = JwtUtils.parseJWT(Authorization,signKey);
        String openId = (String) claims.get("openId");
        String user_auth = (String) claims.get("user_auth");
        return Result.success();
    }

    @PostMapping("/order/howManyAppeal")
    public Result howManyAppeal(@RequestHeader String Authorization){
        Claims claims = JwtUtils.parseJWT(Authorization,signKey);
        String openId = (String) claims.get("openId");
        return Result.success(orderService.howManyAppeal(openId));
    }

    @PostMapping("/order/createNewOrder")
    public Result createNewOrder(@RequestHeader String Authorization, @RequestBody Order order){
        Claims claims = JwtUtils.parseJWT(Authorization,signKey);
        String openId = (String) claims.get("openId");
        orderService.createNewOrder(order);
        return Result.success();
    }
    @PostMapping("/order/alterOrder")
    public Result alterOrder(@RequestBody Order order , @RequestHeader String Authorization){
        Claims claims = JwtUtils.parseJWT(Authorization,signKey);
        return Result.success(orderService.alterOrder());
    }
    @PostMapping("/order/getButtonContent")
    public Result getButtonContent(@RequestBody Button_Content button_content) {
        String auth =  button_content.getUser_auth();
        return Result.success(orderService.getButtonContent(auth));
    }
    @PostMapping("/order/getTipContent")
    public Result getTipContent(@RequestBody Tip_Content tip_content) {
        String auth =  tip_content.getUser_auth();
        return Result.success(orderService.getTipContent(auth));
    }

}

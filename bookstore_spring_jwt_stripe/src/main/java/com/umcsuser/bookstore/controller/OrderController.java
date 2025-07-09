package com.umcsuser.bookstore.controller;


import com.umcsuser.bookstore.dto.OrderRequest;
import com.umcsuser.bookstore.models.Order;
import com.umcsuser.bookstore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/place")
    public ResponseEntity<Order> placeOrder(@RequestBody OrderRequest orderRequest, Principal principal) {
        if (orderRequest.getBookId() == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            String username = principal.getName();
            Order order = orderService.order(orderRequest.getBookId(), orderRequest.getQuantity(), username);
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<Void> cancelOrder(@RequestBody OrderRequest orderRequest, Principal principal) {
        if (orderRequest.getBookId() == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            String username = principal.getName();
            boolean success = orderService.cancelOrder(orderRequest.getBookId(), username);
            if (success) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
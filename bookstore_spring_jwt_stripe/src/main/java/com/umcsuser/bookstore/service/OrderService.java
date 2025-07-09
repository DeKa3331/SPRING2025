package com.umcsuser.bookstore.service;

import com.umcsuser.bookstore.models.Order;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    boolean isBookRented(String bookId);

    Optional<Order> findActiveRentalByBookId(String bookId);

    Order order(Long bookId, Integer quantity, String userId);

    boolean returnOrder(String bookId, String userId);

    List<Order> findAll();


    boolean cancelOrder(Long bookId, String username);
}
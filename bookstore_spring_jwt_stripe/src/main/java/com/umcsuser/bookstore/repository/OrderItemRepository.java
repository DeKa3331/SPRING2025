package com.umcsuser.bookstore.repository;

import com.umcsuser.bookstore.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, String> {}

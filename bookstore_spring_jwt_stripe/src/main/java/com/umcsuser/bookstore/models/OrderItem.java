package com.umcsuser.bookstore.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    private Book book;

    private int quantity;
    private double priceAtOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;
}

package com.umcsuser.bookstore.models;

import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @Column(nullable = false, unique = true)
    private String id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;
    @Column(name = "return_date")
    private LocalDateTime returnDate;
    @OneToOne(mappedBy = "order")
    private Payment payment;

}
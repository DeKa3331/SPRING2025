package com.umcsuser.bookstore.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {
    @Id
    private String id;

    private String title;
    private String author;
    private String isbn;
    private String category;
    private double price;
    private int stock;
    private boolean active;
}

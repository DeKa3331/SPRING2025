package com.umcsuser.bookstore.repository;

import com.umcsuser.bookstore.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, String> {}

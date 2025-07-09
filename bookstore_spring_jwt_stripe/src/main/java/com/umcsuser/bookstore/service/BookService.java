package com.umcsuser.bookstore.service;

import com.umcsuser.bookstore.models.Book;
import java.util.List;
import java.util.Optional;

public interface BookService {
    List<Book> findAll();
    Optional<Book> findById(String id);
    Book save(Book book);
    void softDelete(String id);
}

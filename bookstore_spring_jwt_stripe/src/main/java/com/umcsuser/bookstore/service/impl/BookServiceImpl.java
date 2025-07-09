package com.umcsuser.bookstore.service.impl;

import com.umcsuser.bookstore.models.Book;
import com.umcsuser.bookstore.repository.BookRepository;
import com.umcsuser.bookstore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll().stream()
                .filter(Book::isActive).toList();
    }

    @Override
    public Optional<Book> findById(String id) {
        return bookRepository.findById(id).filter(Book::isActive);
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == null || book.getId().isBlank()) {
            book.setId(UUID.randomUUID().toString());
            book.setActive(true);
        }
        return bookRepository.save(book);
    }

    @Override
    public void softDelete(String id) {
        bookRepository.findById(id).ifPresent(book -> {
            book.setActive(false);
            bookRepository.save(book);
        });
    }
}

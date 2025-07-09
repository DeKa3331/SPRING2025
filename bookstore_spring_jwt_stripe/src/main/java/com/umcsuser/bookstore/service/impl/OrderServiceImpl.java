package com.umcsuser.bookstore.service.impl;

import com.umcsuser.bookstore.models.Order;
import com.umcsuser.bookstore.models.User;
import com.umcsuser.bookstore.models.Book;
import com.umcsuser.bookstore.repository.OrderRepository;
import com.umcsuser.bookstore.repository.UserRepository;
import com.umcsuser.bookstore.repository.BookRepository;
import com.umcsuser.bookstore.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;




@Service
public class OrderServiceImpl implements OrderService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(BookRepository bookRepository, UserRepository userRepository, OrderRepository orderRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public boolean isBookRented(String bookId) {
        return orderRepository.findByBookIdAndReturnDateIsNull(bookId).isPresent();
    }

    @Override
    public Optional<Order> findActiveRentalByBookId(String bookId) {
        return orderRepository.findByBookIdAndReturnDateIsNull(bookId);
    }

    @Override
    @Transactional
    public Order order(Long bookId, Integer quantity, String username) {
        Book book = bookRepository.findById(String.valueOf(bookId))
                .orElseThrow(() -> new EntityNotFoundException("Book not found. ID: " + bookId));
        User user = userRepository.findByLogin(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with login: " + username));
        Order newOrder = Order.builder()
                .id(UUID.randomUUID().toString())
                .book(book)
                .user(user)
                .quantity(quantity)
                .orderDate(LocalDateTime.now())
                .returnDate(null)
                .build();
        return orderRepository.save(newOrder);
    }

    @Override
    @Transactional
    public boolean returnOrder(String bookId, String userId) {
        Optional<Order> orderOpt = orderRepository.findByBookIdAndReturnDateIsNull(bookId);
        if (orderOpt.isPresent() && orderOpt.get().getUser().getId().equals(userId)) {
            Order order = orderOpt.get();
            order.setReturnDate(LocalDateTime.now());
            orderRepository.save(order);
            return true;
        }
        return false;
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    @Transactional
    public boolean cancelOrder(Long bookId, String username) {
        User user = userRepository.findByLogin(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with login: " + username));
        Optional<Order> orderOpt = orderRepository.findByBookIdAndUserIdAndReturnDateIsNull(String.valueOf(bookId), user.getId());
        if (orderOpt.isPresent()) {
            orderRepository.delete(orderOpt.get());
            return true;
        }
        return false;
    }
}
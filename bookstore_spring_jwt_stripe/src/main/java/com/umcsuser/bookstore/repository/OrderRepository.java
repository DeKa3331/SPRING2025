package com.umcsuser.bookstore.repository;

import com.umcsuser.bookstore.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface OrderRepository extends JpaRepository<Order, String> {
    // Methods findAll(), findById(), save(), deleteById() fromJpaRepository.
    Optional<Order> findByBookIdAndReturnDateIsNull(String bookId);
    Optional<Order> findByBookIdAndUserIdAndReturnDateIsNull(String bookId, String userId);
    boolean existsByBookIdAndReturnDateIsNull(String bookId);
    @Query("SELECT r.book.id FROM Order r WHERE r.returnDate IS NULL")
    Set<String> findOrderedBookbyIds();



}

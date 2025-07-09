package com.umcsuser.bookstore.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import com.umcsuser.bookstore.models.Order;
import com.umcsuser.bookstore.models.*;
import com.umcsuser.bookstore.repository.PaymentRepository;
import com.umcsuser.bookstore.repository.OrderRepository;
import com.umcsuser.bookstore.service.PaymentService;
import com.umcsuser.bookstore.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderService orderService;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Value("${stripe.api-key}")
    private String apiKey;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @Override
    @Transactional
    public String createCheckoutSession(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));

        Stripe.apiKey = apiKey;

        int amount = calculateAmount(order);


        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName("Order " + orderId)
                        .build();

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency("pln")
                        .setUnitAmount((long) amount)
                        .setProductData(productData)
                        .build();

        SessionCreateParams.LineItem lineItem =
                SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(priceData)
                        .build();

        SessionCreateParams params = SessionCreateParams.builder()
                .addLineItem(lineItem)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .putMetadata("orderId", orderId)
                .setSuccessUrl("http://localhost:8080/api/payments/success")
                .setCancelUrl("http://localhost:8080/api/payments/cancel")
                .build();

        try {
            Session session = Session.create(params);

            Payment payment = Payment.builder()
                    .id(UUID.randomUUID().toString())
                    .amount(amount)
                    .createdAt(LocalDateTime.now())
                    .order(order)
                    .stripeSessionId(session.getId())
                    .status(PaymentStatus.PENDING)
                    .build();

            paymentRepository.save(payment);
            return session.getUrl();
        } catch (Exception e) {
            throw new RuntimeException("Stripe session creation failed", e);
        }
    }

    @Override
    @Transactional
    public void handleWebhook(String payload, String signature) {
        Stripe.apiKey = apiKey;
        Event event;

        try {
            event = Webhook.constructEvent(payload, signature, webhookSecret);
        } catch (SignatureVerificationException e) {
            throw new RuntimeException("Invalid signature", e);
        }

        if ("checkout.session.completed".equals(event.getType())) {
            StripeObject stripeObject = event.getDataObjectDeserializer().getObject().orElseThrow();
            String sessionId = ((Session) stripeObject).getId();

            if (sessionId != null) {
                paymentRepository.findByStripeSessionId(sessionId).ifPresent(payment -> {
                    payment.setStatus(PaymentStatus.PAID);
                    payment.setPaidAt(LocalDateTime.now());
                    paymentRepository.save(payment);

                    Order order = payment.getOrder();
                    orderService.returnOrder(order.getBook().getId(), order.getUser().getId());
                });
            }
        }
    }


    public int calculateAmount(Order order) {
        LocalDateTime rentDate = order.getOrderDate();
        LocalDateTime returnDate = order.getReturnDate() != null ? order.getReturnDate() : LocalDateTime.now();
        long days = java.time.temporal.ChronoUnit.DAYS.between(rentDate, returnDate);
        if (days <= 0) days = 1;
        double pricePerDay = order.getBook().getPrice();
        return (int) (days * pricePerDay * 100);
    }
}

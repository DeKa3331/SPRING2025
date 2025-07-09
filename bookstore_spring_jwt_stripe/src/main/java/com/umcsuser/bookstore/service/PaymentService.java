package com.umcsuser.bookstore.service;


import com.stripe.model.checkout.Session;
import com.umcsuser.bookstore.models.Payment;

public interface PaymentService {
    String createCheckoutSession(String rentalId);
    void handleWebhook(String payload, String sigHeader);
}
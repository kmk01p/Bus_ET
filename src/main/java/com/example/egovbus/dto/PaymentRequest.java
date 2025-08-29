package com.example.egovbus.dto;

import com.example.egovbus.model.User;
import com.example.egovbus.model.Reservation;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentRequest {
    private User user;
    private Reservation reservation;
    private BigDecimal amount;
    private String paymentMethod;
    private String cbeAccountNumber;
    private String telebirrPhone;
}
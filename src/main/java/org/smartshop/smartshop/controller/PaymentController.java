package org.smartshop.smartshop.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.smartshop.smartshop.dto.payment.CreatePaymentRequestDTO;
import org.smartshop.smartshop.dto.payment.PaymentResponseDTO;
import org.smartshop.smartshop.service.interf.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> createPayment(@Valid @RequestBody CreatePaymentRequestDTO dto) {
        PaymentResponseDTO payment = paymentService.createPayment(dto);
        return new ResponseEntity<>(payment, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable Long id) {
        PaymentResponseDTO payment = paymentService.findById(id);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByOrderId(@PathVariable Long orderId) {
        List<PaymentResponseDTO> payments = paymentService.findByOrderId(orderId);
        return ResponseEntity.ok(payments);
    }

    @PatchMapping("/{id}/cash")
    public ResponseEntity<PaymentResponseDTO> markPaymentAsCashed(@PathVariable Long id) {
        PaymentResponseDTO payment = paymentService.markAsCashed(id);
        return ResponseEntity.ok(payment);
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<PaymentResponseDTO> markPaymentAsRejected(@PathVariable Long id) {
        PaymentResponseDTO payment = paymentService.markAsRejected(id);
        return ResponseEntity.ok(payment);
    }

}
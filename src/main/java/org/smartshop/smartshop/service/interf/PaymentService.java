package org.smartshop.smartshop.service.interf;

import org.smartshop.smartshop.dto.payment.CreatePaymentRequestDTO;
import org.smartshop.smartshop.dto.payment.PaymentResponseDTO;

import java.util.List;

public interface PaymentService {

    PaymentResponseDTO createPayment(CreatePaymentRequestDTO dto);

    PaymentResponseDTO findById(Long id);

    List<PaymentResponseDTO> findByOrderId(Long orderId);

    PaymentResponseDTO markAsCashed(Long paymentId);

    PaymentResponseDTO markAsRejected(Long paymentId);

}
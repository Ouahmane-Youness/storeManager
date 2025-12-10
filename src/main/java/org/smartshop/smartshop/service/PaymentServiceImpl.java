package org.smartshop.smartshop.service;


import lombok.RequiredArgsConstructor;
import org.smartshop.smartshop.dto.payment.CreatePaymentRequestDTO;
import org.smartshop.smartshop.dto.payment.PaymentResponseDTO;
import org.smartshop.smartshop.entity.Order;
import org.smartshop.smartshop.entity.Payment;
import org.smartshop.smartshop.enums.OrderStatus;
import org.smartshop.smartshop.enums.PaymentStatus;
import org.smartshop.smartshop.exception.BusinessException;
import org.smartshop.smartshop.exception.ResourceNotFoundException;
import org.smartshop.smartshop.mapper.PaymentMapper;
import org.smartshop.smartshop.repository.OrderRepository;
import org.smartshop.smartshop.repository.PaymentRepository;
import org.smartshop.smartshop.service.interf.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public PaymentResponseDTO createPayment(CreatePaymentRequestDTO dto) {
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + dto.getOrderId()));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Payments can only be added to PENDING orders");
        }

        if (dto.getPaymentMethod().equals("ESPECES") && dto.getAmount().compareTo(BigDecimal.valueOf(20000)) > 0) {
            throw new BusinessException("Cash payments cannot exceed 20,000 DH (legal limit)");
        }

        if (dto.getAmount().compareTo(order.getRemainingAmount()) > 0) {
            throw new BusinessException("Payment amount exceeds remaining amount. Remaining: " + order.getRemainingAmount());
        }

        Integer paymentNumber = paymentRepository.countPaymentsByOrderId(dto.getOrderId()) + 1;

        Payment payment = paymentMapper.toEntity(dto);
        payment.setOrder(order);
        payment.setPaymentNumber(paymentNumber);

        if (dto.getPaymentMethod().equals("ESPECES")) {
            payment.setStatus(PaymentStatus.ENCAISSE);
            payment.setCashDate(dto.getPaymentDate());
        } else {
            payment.setStatus(PaymentStatus.EN_ATTENTE);
        }

        Payment savedPayment = paymentRepository.save(payment);

        updateOrderRemainingAmount(order);

        return paymentMapper.toResponseDTO(savedPayment);
    }

    private void updateOrderRemainingAmount(Order order) {
        BigDecimal clearedPayments = paymentRepository.sumClearedPaymentsByOrderId(order.getId());
        BigDecimal remainingAmount = order.getTotalTTC().subtract(clearedPayments);
        order.setRemainingAmount(remainingAmount);
        orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO findById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));

        return paymentMapper.toResponseDTO(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> findByOrderId(Long orderId) {
        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        return paymentMapper.toResponseDTOList(payments);
    }

    @Override
    public PaymentResponseDTO markAsCashed(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        if (payment.getStatus() != PaymentStatus.EN_ATTENTE) {
            throw new BusinessException("Only pending payments can be marked as cashed");
        }

        payment.setStatus(PaymentStatus.ENCAISSE);
        payment.setCashDate(LocalDate.now());

        Payment cashedPayment = paymentRepository.save(payment);

        updateOrderRemainingAmount(payment.getOrder());

        return paymentMapper.toResponseDTO(cashedPayment);
    }

    @Override
    public PaymentResponseDTO markAsRejected(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        if (payment.getStatus() != PaymentStatus.EN_ATTENTE) {
            throw new BusinessException("Only pending payments can be rejected");
        }

        payment.setStatus(PaymentStatus.REJETE);
        Payment rejectedPayment = paymentRepository.save(payment);

        updateOrderRemainingAmount(payment.getOrder());

        return paymentMapper.toResponseDTO(rejectedPayment);
    }

}

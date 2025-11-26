package org.smartshop.smartshop.repository;


import org.smartshop.smartshop.entity.Payment;
import org.smartshop.smartshop.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByOrderId(Long orderId);

    List<Payment> findByOrderIdAndStatus(Long orderId, PaymentStatus status);

    List<Payment> findByDeletedFalse();

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.order.id = :orderId AND p.status = 'ENCAISSE' AND p.deleted = false")
    BigDecimal sumClearedPaymentsByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.order.id = :orderId AND p.deleted = false")
    Integer countPaymentsByOrderId(@Param("orderId") Long orderId);

}
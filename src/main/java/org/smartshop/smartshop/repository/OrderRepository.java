package org.smartshop.smartshop.repository;


import org.smartshop.smartshop.entity.Order;
import org.smartshop.smartshop.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByClientIdAndDeletedFalse(Long clientId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByClientIdAndStatus(Long clientId, OrderStatus status);

    List<Order> findByDeletedFalse();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.client.id = :clientId AND o.status = 'CONFIRMED' AND o.deleted = false")
    Integer countConfirmedOrdersByClientId(@Param("clientId") Long clientId);

    @Query("SELECT COALESCE(SUM(o.totalTTC), 0) FROM Order o WHERE o.client.id = :clientId AND o.status = 'CONFIRMED' AND o.deleted = false")
    java.math.BigDecimal sumTotalSpentByClientId(@Param("clientId") Long clientId);

}
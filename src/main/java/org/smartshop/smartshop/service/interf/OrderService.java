package org.smartshop.smartshop.service.interf;

import org.smartshop.smartshop.dto.orderdto.CreateOrderRequestDTO;
import org.smartshop.smartshop.dto.orderdto.OrderResponseDTO;
import org.smartshop.smartshop.enums.OrderStatus;

import java.util.List;

public interface OrderService {

    OrderResponseDTO createOrder(CreateOrderRequestDTO dto);

    OrderResponseDTO findById(Long id);

    List<OrderResponseDTO> findByClientId(Long clientId);

    List<OrderResponseDTO> findByStatus(OrderStatus status);

    List<OrderResponseDTO> findAll();

    OrderResponseDTO confirmOrder(Long id);

    OrderResponseDTO cancelOrder(Long id);

}
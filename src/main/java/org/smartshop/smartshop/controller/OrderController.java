package org.smartshop.smartshop.controller;


import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.smartshop.smartshop.dto.orderdto.CreateOrderRequestDTO;
import org.smartshop.smartshop.dto.orderdto.OrderResponseDTO;
import org.smartshop.smartshop.enums.OrderStatus;
import org.smartshop.smartshop.service.interf.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody CreateOrderRequestDTO dto) {
        OrderResponseDTO order = orderService.createOrder(dto);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {
        OrderResponseDTO order = orderService.findById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByClientId(@PathVariable Long clientId) {
        List<OrderResponseDTO> orders = orderService.findByClientId(clientId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByStatus(@PathVariable OrderStatus status) {
        List<OrderResponseDTO> orders = orderService.findByStatus(status);
        return ResponseEntity.ok(orders);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        List<OrderResponseDTO> orders = orderService.findAll();
        return ResponseEntity.ok(orders);
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<OrderResponseDTO> confirmOrder(@PathVariable Long id) {
        OrderResponseDTO order = orderService.confirmOrder(id);
        return ResponseEntity.ok(order);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrderResponseDTO> cancelOrder(@PathVariable Long id) {
        OrderResponseDTO order = orderService.cancelOrder(id);
        return ResponseEntity.ok(order);
    }

}
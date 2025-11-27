package org.smartshop.smartshop.dto.orderdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.smartshop.smartshop.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {

    private Long id;
    private LocalDateTime orderDate;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal vatAmount;
    private BigDecimal totalTTC;
    private OrderStatus status;
    private BigDecimal remainingAmount;
    private String clientName;
    private String promoCode;
    private List<OrderItemResponseDTO> items;

}
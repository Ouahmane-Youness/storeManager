package org.smartshop.smartshop.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.smartshop.smartshop.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDTO {

    private Long id;
    private Integer paymentNumber;
    private BigDecimal amount;
    private String paymentMethod;
    private LocalDate paymentDate;
    private LocalDate cashDate;
    private PaymentStatus status;
    private String reference;
    private String bankName;
    private LocalDate dueDate;

}
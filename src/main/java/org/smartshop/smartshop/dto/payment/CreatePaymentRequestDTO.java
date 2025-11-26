package com.smartshop.dto.payment;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePaymentRequestDTO {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Amount format invalid")
    private BigDecimal amount;

    @NotBlank(message = "Payment method is required")
    @Pattern(regexp = "ESPECES|CHEQUE|VIREMENT", message = "Payment method must be ESPECES, CHEQUE, or VIREMENT")
    private String paymentMethod;

    @NotNull(message = "Payment date is required")
    private LocalDate paymentDate;

    @Size(max = 100, message = "Reference cannot exceed 100 characters")
    private String reference;

    @Size(max = 100, message = "Bank name cannot exceed 100 characters")
    private String bankName;

    private LocalDate dueDate;

}
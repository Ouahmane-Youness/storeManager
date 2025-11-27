package org.smartshop.smartshop.dto.promocodedto;

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
public class CreatePromoCodeRequestDTO {

    @NotBlank(message = "Promo code is required")
    @Pattern(regexp = "PROMO-[A-Z0-9]{4}", message = "Code must follow format: PROMO-XXXX")
    private String code;

    @NotNull(message = "Discount percentage is required")
    @DecimalMin(value = "1.00", message = "Discount must be at least 1.00%")
    @DecimalMax(value = "100.00", message = "Discount cannot exceed 100%")
    @Digits(integer = 3, fraction = 2, message = "Discount format invalid")
    private BigDecimal discountPercentage;

    @NotNull(message = "Valid from date is required")
    private LocalDate validFrom;

    @NotNull(message = "Valid until date is required")
    private LocalDate validUntil;

    @Min(value = 1, message = "Usage limit must be at least 1")
    private Integer usageLimit;

}
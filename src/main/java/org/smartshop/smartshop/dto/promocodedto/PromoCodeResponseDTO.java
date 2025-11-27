package org.smartshop.smartshop.dto.promocodedto;

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
public class PromoCodeResponseDTO {

    private Long id;
    private String code;
    private BigDecimal discountPercentage;
    private LocalDate validFrom;
    private LocalDate validUntil;
    private Boolean active;
    private Integer usageLimit;
    private Integer usageCount;

}
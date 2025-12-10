package org.smartshop.smartshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.smartshop.smartshop.dto.payment.PaymentResponseDTO;
import org.smartshop.smartshop.entity.Payment;
import org.smartshop.smartshop.dto.payment.CreatePaymentRequestDTO;
import java.util.List;


@Mapper(componentModel = "spring")
public interface PaymentMapper {

    PaymentResponseDTO toResponseDTO(Payment payment);

    List<PaymentResponseDTO> toResponseDTOList(List<Payment> payments);

    @Mapping(target = "paymentNumber", ignore = true)
    @Mapping(target = "cashDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "order", ignore = true)
    Payment toEntity(CreatePaymentRequestDTO dto);

}
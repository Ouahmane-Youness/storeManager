package org.smartshop.smartshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.smartshop.smartshop.dto.orderdto.OrderResponseDTO;
import org.smartshop.smartshop.entity.Order;

import java.util.List;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {

    @Mapping(source = "client.name", target = "clientName")
    @Mapping(source = "promoCode.code", target = "promoCode")
    OrderResponseDTO toResponseDTO(Order order);

    List<OrderResponseDTO> toResponseDTOList(List<Order> orders);

}
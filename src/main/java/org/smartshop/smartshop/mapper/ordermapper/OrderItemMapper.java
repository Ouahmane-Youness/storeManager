package org.smartshop.smartshop.mapper.ordermapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.smartshop.smartshop.dto.orderdto.OrderItemResponseDTO;
import org.smartshop.smartshop.entity.OrderItem;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "product.name", target = "productName")
    OrderItemResponseDTO toResponseDTO(OrderItem orderItem);

    List<OrderItemResponseDTO> toResponseDTOList(List<OrderItem> orderItems);

}
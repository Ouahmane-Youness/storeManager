package org.smartshop.smartshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.smartshop.smartshop.dto.productdto.CreateProductRequestDTO;
import org.smartshop.smartshop.dto.productdto.ProductResponseDTO;
import org.smartshop.smartshop.dto.productdto.UpdateProductRequestDTO;
import org.smartshop.smartshop.entity.Product;

import java.util.List;


@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductResponseDTO toResponseDTO(Product product);

    List<ProductResponseDTO> toResponseDTOList(List<Product> products);

    Product toEntity(CreateProductRequestDTO dto);

    @Mapping(target = "name", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "price", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "stock", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "active", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(UpdateProductRequestDTO dto, @MappingTarget Product product);

}
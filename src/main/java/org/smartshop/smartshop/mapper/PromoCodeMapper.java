package org.smartshop.smartshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.smartshop.smartshop.dto.promocodedto.CreatePromoCodeRequestDTO;
import org.smartshop.smartshop.dto.promocodedto.PromoCodeResponseDTO;
import org.smartshop.smartshop.entity.PromoCode;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PromoCodeMapper {

    PromoCodeResponseDTO toResponseDTO(PromoCode promoCode);

    List<PromoCodeResponseDTO> toResponseDTOList(List<PromoCode> promoCodes);

    @Mapping(target = "orders", ignore = true)
    PromoCode toEntity(CreatePromoCodeRequestDTO dto);

}
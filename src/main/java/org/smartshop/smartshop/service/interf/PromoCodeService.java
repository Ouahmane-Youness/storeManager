package org.smartshop.smartshop.service.interf;

import org.smartshop.smartshop.dto.promocodedto.CreatePromoCodeRequestDTO;
import org.smartshop.smartshop.dto.promocodedto.PromoCodeResponseDTO;

import java.util.List;

public interface PromoCodeService {

    PromoCodeResponseDTO createPromoCode(CreatePromoCodeRequestDTO dto);

    PromoCodeResponseDTO findById(Long id);

    PromoCodeResponseDTO findByCode(String code);

    List<PromoCodeResponseDTO> findAllActive();

    List<PromoCodeResponseDTO> findAll();

    void activatePromoCode(Long id);

    void deactivatePromoCode(Long id);

}
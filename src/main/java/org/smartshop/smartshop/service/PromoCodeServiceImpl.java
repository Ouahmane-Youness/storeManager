package org.smartshop.smartshop.service;


import lombok.RequiredArgsConstructor;
import org.smartshop.smartshop.dto.promocodedto.CreatePromoCodeRequestDTO;
import org.smartshop.smartshop.dto.promocodedto.PromoCodeResponseDTO;
import org.smartshop.smartshop.entity.PromoCode;
import org.smartshop.smartshop.exception.DuplicateResourceException;
import org.smartshop.smartshop.exception.ResourceNotFoundException;
import org.smartshop.smartshop.mapper.PromoCodeMapper;
import org.smartshop.smartshop.repository.PromoCodeRepository;
import org.smartshop.smartshop.service.interf.PromoCodeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PromoCodeServiceImpl implements PromoCodeService {

    private final PromoCodeRepository promoCodeRepository;
    private final PromoCodeMapper promoCodeMapper;

    @Override
    public PromoCodeResponseDTO createPromoCode(CreatePromoCodeRequestDTO dto) {
        if (promoCodeRepository.existsByCode(dto.getCode())) {
            throw new DuplicateResourceException("Promo code already exists: " + dto.getCode());
        }

        PromoCode promoCode = promoCodeMapper.toEntity(dto);
        promoCode.setActive(true);
        promoCode.setUsageCount(0);

        PromoCode savedPromoCode = promoCodeRepository.save(promoCode);
        return promoCodeMapper.toResponseDTO(savedPromoCode);
    }

    @Override
    @Transactional(readOnly = true)
    public PromoCodeResponseDTO findById(Long id) {
        PromoCode promoCode = promoCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promo code not found with id: " + id));

        return promoCodeMapper.toResponseDTO(promoCode);
    }

    @Override
    @Transactional(readOnly = true)
    public PromoCodeResponseDTO findByCode(String code) {
        PromoCode promoCode = promoCodeRepository.findByCodeAndDeletedFalse(code)
                .orElseThrow(() -> new ResourceNotFoundException("Promo code not found: " + code));

        return promoCodeMapper.toResponseDTO(promoCode);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromoCodeResponseDTO> findAllActive() {
        List<PromoCode> promoCodes = promoCodeRepository.findByActiveTrueAndDeletedFalse();
        return promoCodeMapper.toResponseDTOList(promoCodes);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromoCodeResponseDTO> findAll() {
        List<PromoCode> promoCodes = promoCodeRepository.findByDeletedFalse();
        return promoCodeMapper.toResponseDTOList(promoCodes);
    }

    @Override
    public void activatePromoCode(Long id) {
        PromoCode promoCode = promoCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promo code not found with id: " + id));

        promoCode.setActive(true);
        promoCodeRepository.save(promoCode);
    }

    @Override
    public void deactivatePromoCode(Long id) {
        PromoCode promoCode = promoCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promo code not found with id: " + id));

        promoCode.setActive(false);
        promoCodeRepository.save(promoCode);
    }

}
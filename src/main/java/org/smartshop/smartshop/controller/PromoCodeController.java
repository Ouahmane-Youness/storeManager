package org.smartshop.smartshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.smartshop.smartshop.dto.promocodedto.CreatePromoCodeRequestDTO;
import org.smartshop.smartshop.dto.promocodedto.PromoCodeResponseDTO;
import org.smartshop.smartshop.service.interf.PromoCodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promo-codes")
@RequiredArgsConstructor
public class PromoCodeController {

    private final PromoCodeService promoCodeService;

    @PostMapping
    public ResponseEntity<PromoCodeResponseDTO> createPromoCode(@Valid @RequestBody CreatePromoCodeRequestDTO dto) {
        PromoCodeResponseDTO promoCode = promoCodeService.createPromoCode(dto);
        return new ResponseEntity<>(promoCode, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PromoCodeResponseDTO> getPromoCodeById(@PathVariable Long id) {
        PromoCodeResponseDTO promoCode = promoCodeService.findById(id);
        return ResponseEntity.ok(promoCode);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<PromoCodeResponseDTO> getPromoCodeByCode(@PathVariable String code) {
        PromoCodeResponseDTO promoCode = promoCodeService.findByCode(code);
        return ResponseEntity.ok(promoCode);
    }

    @GetMapping("/active")
    public ResponseEntity<List<PromoCodeResponseDTO>> getAllActivePromoCodes() {
        List<PromoCodeResponseDTO> promoCodes = promoCodeService.findAllActive();
        return ResponseEntity.ok(promoCodes);
    }

    @GetMapping
    public ResponseEntity<List<PromoCodeResponseDTO>> getAllPromoCodes() {
        List<PromoCodeResponseDTO> promoCodes = promoCodeService.findAll();
        return ResponseEntity.ok(promoCodes);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activatePromoCode(@PathVariable Long id) {
        promoCodeService.activatePromoCode(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivatePromoCode(@PathVariable Long id) {
        promoCodeService.deactivatePromoCode(id);
        return ResponseEntity.ok().build();
    }

}
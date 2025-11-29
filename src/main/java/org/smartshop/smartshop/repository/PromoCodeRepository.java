package org.smartshop.smartshop.repository;

import org.smartshop.smartshop.entity.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {

    Optional<PromoCode> findByCode(String code);

    Optional<PromoCode> findByCodeAndDeletedFalse(String code);

    boolean existsByCode(String code);

    List<PromoCode> findByActiveTrueAndDeletedFalse();
    List<PromoCode> findByDeletedFalse();

    @Query("SELECT p FROM PromoCode p WHERE p.code = :code AND p.active = true " +
            "AND p.deleted = false AND :currentDate BETWEEN p.validFrom AND p.validUntil " +
            "AND (p.usageLimit IS NULL OR p.usageCount < p.usageLimit)")
    Optional<PromoCode> findValidPromoCode(@Param("code") String code, @Param("currentDate") LocalDate currentDate);


}
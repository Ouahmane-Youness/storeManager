package org.smartshop.smartshop.repository;

import org.smartshop.smartshop.entity.Client;
import org.smartshop.smartshop.enums.CustomerTier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByEmail(String email);

    Optional<Client> findByEmailAndDeletedFalse(String email);

    boolean existsByEmail(String email);

    List<Client> findByTier(CustomerTier tier);

    List<Client> findByDeletedFalse();

    Optional<Client> findByUserId(Long userId);

}

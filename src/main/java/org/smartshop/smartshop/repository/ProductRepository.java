package org.smartshop.smartshop.repository;


import org.smartshop.smartshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByIsActiveTrueAndDeletedFalse();

    Optional<Product> findByIdAndDeletedFalse(Long id);

    List<Product> findByNameContainingIgnoreCaseAndDeletedFalse(String keyword);

    List<Product> findByDeletedFalse();

}
package org.smartshop.smartshop.service.interf;

import org.smartshop.smartshop.dto.productdto.CreateProductRequestDTO;
import org.smartshop.smartshop.dto.productdto.ProductResponseDTO;
import org.smartshop.smartshop.dto.productdto.UpdateProductRequestDTO;

import java.util.List;

public interface ProductService {

    ProductResponseDTO createProduct(CreateProductRequestDTO dto);

    ProductResponseDTO findById(Long id);

    List<ProductResponseDTO> findAllActive();

    List<ProductResponseDTO> findAll();

    List<ProductResponseDTO> searchByName(String keyword);

    ProductResponseDTO updateProduct(Long id, UpdateProductRequestDTO dto);

    void deleteProduct(Long id);

}
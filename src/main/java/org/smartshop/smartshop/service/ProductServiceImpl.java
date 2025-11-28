package org.smartshop.smartshop.service;


import lombok.RequiredArgsConstructor;

import org.smartshop.smartshop.dto.productdto.CreateProductRequestDTO;
import org.smartshop.smartshop.dto.productdto.ProductResponseDTO;
import org.smartshop.smartshop.dto.productdto.UpdateProductRequestDTO;
import org.smartshop.smartshop.entity.Product;
import org.smartshop.smartshop.exception.ResourceNotFoundException;
import org.smartshop.smartshop.mapper.ProductMapper;
import org.smartshop.smartshop.repository.ProductRepository;
import org.smartshop.smartshop.service.interf.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponseDTO createProduct(CreateProductRequestDTO dto) {
        Product product = productMapper.toEntity(dto);
        product.setActive(true);

        Product savedProduct = productRepository.save(product);
        return productMapper.toResponseDTO(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO findById(Long id) {
        Product product = productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        return productMapper.toResponseDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findAllActive() {
        List<Product> products = productRepository.findByActiveTrueAndDeletedFalse();
        return productMapper.toResponseDTOList(products);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findAll() {
        List<Product> products = productRepository.findByDeletedFalse();
        return productMapper.toResponseDTOList(products);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> searchByName(String keyword) {
        List<Product> products = productRepository.findByNameContainingIgnoreCaseAndDeletedFalse(keyword);
        return productMapper.toResponseDTOList(products);
    }

    @Override
    public ProductResponseDTO updateProduct(Long id, UpdateProductRequestDTO dto) {
        Product product = productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        productMapper.updateEntityFromDTO(dto, product);
        Product updatedProduct = productRepository.save(product);

        return productMapper.toResponseDTO(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setDeleted(true);
        productRepository.save(product);
    }

}
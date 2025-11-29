package org.smartshop.smartshop.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.smartshop.smartshop.dto.orderdto.CreateOrderRequestDTO;
import org.smartshop.smartshop.dto.orderdto.OrderItemDTO;
import org.smartshop.smartshop.dto.orderdto.OrderResponseDTO;
import org.smartshop.smartshop.entity.Client;
import org.smartshop.smartshop.entity.OrderItem;
import org.smartshop.smartshop.entity.Product;
import org.smartshop.smartshop.entity.PromoCode;
import org.smartshop.smartshop.enums.CustomerTier;
import org.smartshop.smartshop.enums.OrderStatus;
import org.smartshop.smartshop.exception.BusinessException;
import org.smartshop.smartshop.exception.ResourceNotFoundException;
import org.smartshop.smartshop.repository.ClientRepository;
import org.smartshop.smartshop.repository.ProductRepository;
import org.smartshop.smartshop.repository.PromoCodeRepository;
import org.smartshop.smartshop.service.interf.OrderService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;

    private final PromoCodeRepository promoCodeRepository;

    @Override
    public OrderResponseDTO createOrder(CreateOrderRequestDTO dto) {

        Client client = clientRepository.findById(dto.getClientId()).orElseThrow(()->
                new ResourceNotFoundException("client not found with id" + dto.getClientId())
        );

        if(dto.getItems() ==  null || dto.getItems().isEmpty())
        {
            throw new BusinessException("order must contain atleast one item");
        }

        List<OrderItem> items = new ArrayList<>();
        BigDecimal subTotal = BigDecimal.ZERO;

        for(OrderItemDTO item : dto.getItems())
        {
            Product product = productRepository.findById(item.getProductId()).orElseThrow( ()->
                    new ResourceNotFoundException("product not found with id" + item.getProductId())
            );

            if(!product.isActive())
            {
                throw new BusinessException("product is not active" + product.getName());
            }

            if(product.getStock() < item.getQuantity())
            {
                throw new BusinessException("Inssuficient stock for product" + product.getName() + " available stock" +
                        product.getStock() + "quantity ordered" + item.getQuantity());
            }

            BigDecimal itemTotal = product.getPrice().
                    multiply(BigDecimal.valueOf(item.getQuantity())).
                    setScale(2, RoundingMode.HALF_UP);

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(item.getQuantity())
                    .unitPrice(product.getPrice())
                    .totalPrice(itemTotal)
                    .build();

            items.add(orderItem);

        }

        BigDecimal loyaltyDiscount = calculateLoyaltyDiscount(client, subtotal);

//        PromoCode code = promoCodeRepository.existsByCode(dto.getPromoCode());


    }

    private BigDecimal calculateLoyaltyDiscount(Client client, BigDecimal subtotal)
    {
        CustomerTier tier = client.getTier();
        BigDecimal discountPercentage = BigDecimal.ZERO;

        switch (tier)
        {
            case SILVER:
                if(subtotal.compareTo(BigDecimal.valueOf(500)) >= 0)
                {
                    discountPercentage = BigDecimal.valueOf(5);
                }
                break;

            case GOLD:
                if(subtotal.compareTo(BigDecimal.valueOf(800)) >= 0)
                {
                    discountPercentage = BigDecimal.valueOf(10);
                }

            case PLATINUM:
                if(subtotal.compareTo(BigDecimal.valueOf(1200)) >= 0)
                {
                    discountPercentage = BigDecimal.valueOf(15);
                }
        }

        BigDecimal discountAmount =  discountPercentage.multiply(subtotal).divide(BigDecimal.valueOf(100), 2,RoundingMode.HALF_UP);
    }

    @Override
    public OrderResponseDTO findById(Long id) {
        return null;
    }

    @Override
    public List<OrderResponseDTO> findByClientId(Long clientId) {
        return List.of();
    }

    @Override
    public List<OrderResponseDTO> findByStatus(OrderStatus status) {
        return List.of();
    }

    @Override
    public List<OrderResponseDTO> findAll() {
        return List.of();
    }

    @Override
    public OrderResponseDTO confirmOrder(Long id) {
        return null;
    }

    @Override
    public OrderResponseDTO cancelOrder(Long id) {
        return null;
    }
}

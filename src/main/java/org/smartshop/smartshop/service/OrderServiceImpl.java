package org.smartshop.smartshop.service;

import lombok.RequiredArgsConstructor;
import org.smartshop.smartshop.config.VatConfig;
import org.smartshop.smartshop.dto.orderdto.CreateOrderRequestDTO;
import org.smartshop.smartshop.dto.orderdto.OrderItemDTO;
import org.smartshop.smartshop.dto.orderdto.OrderResponseDTO;
import org.smartshop.smartshop.entity.*;
import org.smartshop.smartshop.enums.CustomerTier;
import org.smartshop.smartshop.enums.OrderStatus;
import org.smartshop.smartshop.exception.BusinessException;
import org.smartshop.smartshop.exception.ResourceNotFoundException;
import org.smartshop.smartshop.mapper.ordermapper.OrderMapper;
import org.smartshop.smartshop.repository.ClientRepository;
import org.smartshop.smartshop.repository.OrderRepository;
import org.smartshop.smartshop.repository.ProductRepository;
import org.smartshop.smartshop.repository.PromoCodeRepository;
import org.smartshop.smartshop.service.interf.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;

    private final PromoCodeRepository promoCodeRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final VatConfig vatConfig;


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
            subTotal = subTotal.add(itemTotal);

        }

        BigDecimal loyaltyDiscount = calculateLoyaltyDiscount(client, subTotal);

        BigDecimal promoDiscount = BigDecimal.ZERO;
        PromoCode promoCode = null;
        BigDecimal appliedPromoPercentage = null;

        if (dto.getPromoCode() != null && !dto.getPromoCode().isEmpty()) {
            promoCode = promoCodeRepository.findValidPromoCode(dto.getPromoCode(), LocalDate.now())
                    .orElseThrow(() -> new BusinessException("Invalid or expired promo code: " + dto.getPromoCode()));

            appliedPromoPercentage = promoCode.getDiscountPercentage();
            promoDiscount = subTotal.multiply(promoCode.getDiscountPercentage())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        BigDecimal totalDiscount = loyaltyDiscount.add(promoDiscount);
        BigDecimal amountAfterDiscount = subTotal.subtract(totalDiscount);

        BigDecimal vatAmount = amountAfterDiscount.multiply(vatConfig.getRate())
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalTTC = amountAfterDiscount.add(vatAmount);

        Order order = Order.builder()
                .client(client)
                .promoCode(promoCode)
                .orderDate(LocalDateTime.now())
                .subtotal(subTotal)
                .discountAmount(totalDiscount)
                .vatAmount(vatAmount)
                .totalTTC(totalTTC)
                .status(OrderStatus.PENDING)
                .remainingAmount(totalTTC)
                .appliedDiscountPercentage(appliedPromoPercentage)
                .items(new ArrayList<>())
                .payments(new ArrayList<>())
                .build();

        for (OrderItem item : items) {
            item.setOrder(order);
            order.getItems().add(item);
        }

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponseDTO(savedOrder);
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

        return  discountPercentage.multiply(subtotal).divide(BigDecimal.valueOf(100), 2,RoundingMode.HALF_UP);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        return orderMapper.toResponseDTO(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> findByClientId(Long clientId) {
        List<Order> orders = orderRepository.findByClientIdAndDeletedFalse(clientId);
        return orderMapper.toResponseDTOList(orders);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> findByStatus(OrderStatus status) {
        List<Order> orders = orderRepository.findByStatus(status);
        return orderMapper.toResponseDTOList(orders);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> findAll() {
        List<Order> orders = orderRepository.findByDeletedFalse();
        return orderMapper.toResponseDTOList(orders);
    }

    @Override
    public OrderResponseDTO confirmOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Only PENDING orders can be confirmed. Current status: " + order.getStatus());
        }

        if (order.getRemainingAmount().compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessException("Order cannot be confirmed. Remaining amount: " + order.getRemainingAmount());
        }

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            if (product.getStock() < item.getQuantity()) {
                throw new BusinessException("Insufficient stock for product: " + product.getName());
            }
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(OrderStatus.CONFIRMED);
        Order confirmedOrder = orderRepository.save(order);

        updateClientStatistics(order.getClient(), order.getTotalTTC());

        if (order.getPromoCode() != null) {
            PromoCode promoCode = order.getPromoCode();
            promoCode.setUsageCount(promoCode.getUsageCount() + 1);
            promoCodeRepository.save(promoCode);
        }

        return orderMapper.toResponseDTO(confirmedOrder);
    }

    private void updateClientStatistics(Client client, BigDecimal orderTotal) {
        client.setTotalOrders(client.getTotalOrders() + 1);
        client.setTotalSpent(client.getTotalSpent().add(orderTotal));

        if (client.getFirstOrderDate() == null) {
            client.getFirstOrderDate(LocalDateTime.now());
        }
        client.setLastOrderDate(LocalDateTime.now());

        updateClientTier(client);

        clientRepository.save(client);
    }

    private void updateClientTier(Client client) {
        Integer totalOrders = client.getTotalOrders();
        BigDecimal totalSpent = client.getTotalSpent();

        if (totalOrders >= 20 || totalSpent.compareTo(BigDecimal.valueOf(15000)) >= 0) {
            client.setTier(CustomerTier.PLATINUM);
        } else if (totalOrders >= 10 || totalSpent.compareTo(BigDecimal.valueOf(5000)) >= 0) {
            client.setTier(CustomerTier.GOLD);
        } else if (totalOrders >= 3 || totalSpent.compareTo(BigDecimal.valueOf(1000)) >= 0) {
            client.setTier(CustomerTier.SILVER);
        }
    }

    @Override
    public OrderResponseDTO cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Only PENDING orders can be canceled. Current status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELED);
        Order canceledOrder = orderRepository.save(order);

        return orderMapper.toResponseDTO(canceledOrder);
    }
}

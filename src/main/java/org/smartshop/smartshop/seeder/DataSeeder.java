package org.smartshop.smartshop.seeder;

import lombok.RequiredArgsConstructor;
import org.smartshop.smartshop.entity.*;
import org.smartshop.smartshop.enums.*;
import org.smartshop.smartshop.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    // Removed OrderItemRepository - not needed due to CascadeType.ALL
    private final PaymentRepository paymentRepository;
    private final PromoCodeRepository promoCodeRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            System.out.println("🌱 Seeding database...");

            seedUsersAndClients();
            List<Product> products = seedProducts();
            seedPromoCodes();
            seedOrdersAndPayments(products);

            System.out.println("✅ Database seeding completed!");
        }
    }

    private void seedUsersAndClients() {
        // ADMIN
        User admin = User.builder()
                .username("admin")
                .password("admin123") // Remember to encode this if using BCrypt
                .role(UserRole.ADMIN)
                .build();
        userRepository.save(admin);

        // CLIENTS
        createClient("TechSolutions", "contact@techsolutions.ma", "client1", CustomerTier.PLATINUM);
        createClient("GreenPulse", "info@greenpulse.com", "client2", CustomerTier.GOLD);
        createClient("MicroServices SARL", "sales@microservices.ma", "client3", CustomerTier.SILVER);
        createClient("AlphaOne", "support@alphaone.com", "client4", CustomerTier.BASIC);
    }

    private void createClient(String name, String email, String username, CustomerTier tier) {
        User user = User.builder()
                .username(username)
                .password("123456")
                .role(UserRole.Client)
                .build();

        user = userRepository.save(user);

        Client client = Client.builder()
                .name(name)
                .email(email)
                .tier(tier)
                .totalOrders(tier == CustomerTier.BASIC ? 0 : 5)
                .totalSpent(tier == CustomerTier.BASIC ? BigDecimal.ZERO : BigDecimal.valueOf(10000))
                .firstOrderDate(LocalDateTime.now().minusMonths(3))
                .lastOrderDate(LocalDateTime.now().minusDays(2))
                .user(user)
                .build();

        clientRepository.save(client);
    }

    private List<Product> seedProducts() {
        List<Product> products = Arrays.asList(
                createProduct("Laptop HP EliteBook", 8500.00, 50),
                createProduct("Dell PowerEdge Server", 15000.00, 10),
                createProduct("Logitech Wireless Mouse", 250.00, 200),
                createProduct("Samsung 27 Monitor", 2200.00, 45),
                createProduct("Cisco Switch 24 Ports", 4500.00, 20)
        );
        return productRepository.saveAll(products);
    }

    private Product createProduct(String name, double price, int stock) {
        return Product.builder()
                .name(name)
                .price(BigDecimal.valueOf(price))
                .stock(stock)
                .active(true)
                .build();
    }

    private void seedPromoCodes() {
        List<PromoCode> codes = Arrays.asList(
                PromoCode.builder()
                        .code("PROMO-2025")
                        .discountPercentage(BigDecimal.valueOf(10.0))
                        .validFrom(LocalDate.now().minusDays(1))
                        .validUntil(LocalDate.now().plusMonths(1))
                        .usageLimit(100)
                        .active(true)
                        .build(),
                PromoCode.builder()
                        .code("PROMO-SUMM")
                        .discountPercentage(BigDecimal.valueOf(5.0))
                        .validFrom(LocalDate.now().minusDays(10))
                        .validUntil(LocalDate.now().plusDays(10))
                        .usageLimit(50)
                        .active(true)
                        .build()
        );
        promoCodeRepository.saveAll(codes);
    }

    private void seedOrdersAndPayments(List<Product> products) {
        Client client = clientRepository.findAll().stream()
                .filter(c -> c.getName().equals("TechSolutions"))
                .findFirst()
                .orElseThrow();

        createOrderWithPayments(client, products, OrderStatus.CONFIRMED);
        createOrderWithPayments(client, products, OrderStatus.PENDING);
        createOrderWithPayments(client, products, OrderStatus.REJECTED);
        createOrderWithPayments(client, products, OrderStatus.CANCELED);
    }

    private void createOrderWithPayments(Client client, List<Product> products, OrderStatus status) {
        BigDecimal subTotal = BigDecimal.valueOf(5000);
        BigDecimal vat = subTotal.multiply(BigDecimal.valueOf(0.20));
        BigDecimal total = subTotal.add(vat);

        // 1. Create the Order
        Order order = Order.builder()
                .client(client)
                .orderDate(LocalDateTime.now().minusDays(status == OrderStatus.CONFIRMED ? 5 : 0))
                .status(status)
                .subtotal(subTotal)
                .discountAmount(BigDecimal.ZERO)
                .vatAmount(vat)
                .totalTTC(total)
                .remainingAmount(status == OrderStatus.CONFIRMED ? BigDecimal.ZERO : total)
                .build();

        // 2. Create the OrderItem
        OrderItem item1 = OrderItem.builder()
                .order(order)           // Link item to order
                .product(products.get(0))
                .quantity(1)
                .unitPrice(products.get(0).getPrice())
                .totalPrice(products.get(0).getPrice())
                .build();

        // 3. IMPORTANT: Add item to the Order's list (for Cascade to work)
        order.getItems().add(item1);

        // 4. Saving the Order automatically saves the Item
        order = orderRepository.save(order);

        // 5. Create Payments (Optional)
        if (status == OrderStatus.CONFIRMED) {
            Payment p1 = Payment.builder()
                    .order(order)
                    .paymentNumber(1)
                    .amount(total)
                    .paymentMethod("VIREMENT")
                    .paymentDate(LocalDate.now().minusDays(4))
                    .status(PaymentStatus.ENCAISSE)
                    .reference("VIR-12345")
                    .bankName("CIH Bank")
                    .build();
            paymentRepository.save(p1);
        }
    }
}
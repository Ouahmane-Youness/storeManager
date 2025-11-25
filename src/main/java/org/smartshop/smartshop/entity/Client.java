package org.smartshop.smartshop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.smartshop.smartshop.enums.CustomerTier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Entity
@Table(name = "clients")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Client extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String LastName;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerTier tier = CustomerTier.BASIC;

    @Column(nullable = false)
    private int totalOrders = 0;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalSpent = BigDecimal.ZERO;

    private LocalDateTime firstOrderDate;
    private LocalDateTime LastOrderDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "client,", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Order> orders = new ArrayList<>();
}

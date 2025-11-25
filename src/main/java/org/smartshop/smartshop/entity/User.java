package org.smartshop.smartshop.entity;


import jakarta.persistence.*;
import lombok.*;
import org.smartshop.smartshop.enums.UserRole;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User extends BaseEntity{

    @Column(nullable = false, unique = true, length = 50)
    private String userName;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @OneToOne(mappedBy ="user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Client client;
}

package org.smartshop.smartshop.repository;

import org.smartshop.smartshop.entity.User;
import org.smartshop.smartshop.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndDeletedFalse(String username);

    boolean existsByUsername(String username);

    List<User> findByRole(UserRole role);

    List<User> findByDeletedFalse();

}

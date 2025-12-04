package org.trs.therepairsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.trs.therepairsystem.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    
    Optional<User> findByPhone(String phone);

    boolean existsByUsername(String username);

}
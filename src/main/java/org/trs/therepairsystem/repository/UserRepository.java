package org.trs.therepairsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.trs.therepairsystem.pojo.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
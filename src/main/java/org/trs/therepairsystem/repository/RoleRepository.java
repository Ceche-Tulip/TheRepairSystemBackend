package org.trs.therepairsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.trs.therepairsystem.pojo.UserRole;

public interface RoleRepository extends JpaRepository<UserRole, Integer> {
}


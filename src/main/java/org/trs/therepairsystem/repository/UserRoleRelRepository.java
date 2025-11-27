package org.trs.therepairsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.trs.therepairsystem.pojo.UserRoleRel;

import java.util.List;

public interface UserRoleRelRepository extends JpaRepository<UserRoleRel, Long> {
    List<UserRoleRel> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}

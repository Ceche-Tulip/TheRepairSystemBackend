package org.trs.therepairsystem.service;

import org.trs.therepairsystem.pojo.UserRole;

import java.util.List;

public interface RoleService {

    List<UserRole> getAllRoles();

    UserRole createRole(UserRole role);

    void deleteRole(Integer roleId);
}


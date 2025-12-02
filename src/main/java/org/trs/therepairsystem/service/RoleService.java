package org.trs.therepairsystem.service;

import org.trs.therepairsystem.pojo.UserRole;

import java.util.List;

public interface RoleService {

    List<UserRole> getAllRoles();

    UserRole createRole(UserRole role);
    
    UserRole updateRole(Integer roleId, UserRole role);
    
    UserRole getById(Integer roleId);

    void deleteRole(Integer roleId);
}


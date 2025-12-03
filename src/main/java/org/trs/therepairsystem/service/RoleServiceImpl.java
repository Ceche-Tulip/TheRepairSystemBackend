package org.trs.therepairsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trs.therepairsystem.entity.UserRole;
import org.trs.therepairsystem.repository.RoleRepository;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<UserRole> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public UserRole createRole(UserRole role) {
        return roleRepository.save(role);
    }
    
    @Override
    public UserRole updateRole(Integer roleId, UserRole role) {
        role.setId(roleId);
        return roleRepository.save(role);
    }
    
    @Override
    public UserRole getById(Integer roleId) {
        return roleRepository.findById(roleId).orElse(null);
    }

    @Override
    public void deleteRole(Integer roleId) {
        roleRepository.deleteById(roleId);
    }
}


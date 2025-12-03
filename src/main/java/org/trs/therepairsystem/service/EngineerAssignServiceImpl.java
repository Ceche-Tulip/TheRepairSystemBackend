package org.trs.therepairsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trs.therepairsystem.dto.response.engineer.EngineerAreaDTO;
import org.trs.therepairsystem.dto.response.engineer.EngineerFaultDTO;
import org.trs.therepairsystem.dto.response.UserDTO;
import org.trs.therepairsystem.entity.*;
import org.trs.therepairsystem.repository.*;
import org.trs.therepairsystem.web.converter.EngineerAreaConverter;
import org.trs.therepairsystem.web.converter.EngineerFaultConverter;
import org.trs.therepairsystem.web.converter.UserConverter;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EngineerAssignServiceImpl implements EngineerAssignService {
    
    private final EngineerAreaRelRepository engineerAreaRelRepository;
    private final EngineerFaultRelRepository engineerFaultRelRepository;
    private final UserRepository userRepository;
    private final FloorRepository floorRepository;
    private final FaultTypeRepository faultTypeRepository;
    private final UserRoleRelRepository userRoleRelRepository;
    
    /**
     * 验证用户是否具有工程师角色
     */
    private void validateEngineerRole(Long userId) {
        boolean isEngineer = userRoleRelRepository.findByUserId(userId)
                .stream()
                .anyMatch(rel -> "ENGINEER".equals(rel.getRole().getRoleName()));
        
        if (!isEngineer) {
            throw new IllegalArgumentException("用户ID " + userId + " 不是工程师角色，无法进行工程师分配");
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllEngineers() {
        // 查询所有具有ENGINEER角色的用户
        List<UserRoleRel> engineerRoles = userRoleRelRepository.findAll()
                .stream()
                .filter(rel -> "ENGINEER".equals(rel.getRole().getRoleName()))
                .collect(Collectors.toList());
        
        return engineerRoles.stream()
                .map(rel -> UserConverter.toDTO(rel.getUser()))
                .collect(Collectors.toList());
    }
    
    // ========== 区域分配管理 ==========
    
    @Override
    public void assignEngineerToAreas(Long engineerId, List<Long> floorIds) {
        // 验证用户是否存在
        User engineer = userRepository.findById(engineerId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + engineerId));
        
        // 验证用户是否具有工程师角色
        validateEngineerRole(engineerId);
        
        // 删除原有的区域分配
        engineerAreaRelRepository.deleteByEngineerId(engineerId);
        
        // 创建新的分配关系
        for (Long floorId : floorIds) {
            Floor floor = floorRepository.findById(floorId)
                    .orElseThrow(() -> new IllegalArgumentException("楼层不存在: " + floorId));
            
            if (!engineerAreaRelRepository.existsByEngineerIdAndFloorId(engineerId, floorId)) {
                EngineerAreaRel rel = new EngineerAreaRel(null, engineer, floor);
                engineerAreaRelRepository.save(rel);
            }
        }
    }
    
    @Override
    public void removeEngineerFromArea(Long engineerId, Long floorId) {
        engineerAreaRelRepository.deleteByEngineerIdAndFloorId(engineerId, floorId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<EngineerAreaDTO> getEngineerAreas(Long engineerId) {
        List<EngineerAreaRel> rels = engineerAreaRelRepository.findByEngineerId(engineerId);
        return rels.stream()
                .map(EngineerAreaConverter::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<EngineerAreaDTO> getFloorEngineers(Long floorId) {
        List<EngineerAreaRel> rels = engineerAreaRelRepository.findByFloorId(floorId);
        return rels.stream()
                .map(EngineerAreaConverter::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<EngineerAreaDTO> getAllAreaAssignments() {
        List<EngineerAreaRel> rels = engineerAreaRelRepository.findAll();
        return rels.stream()
                .map(EngineerAreaConverter::toDTO)
                .collect(Collectors.toList());
    }
    
    // ========== 故障类型分配管理 ==========
    
    @Override
    public void assignEngineerToFaultTypes(Long engineerId, List<Long> faultTypeIds) {
        // 验证用户是否存在
        User engineer = userRepository.findById(engineerId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + engineerId));
        
        // 验证用户是否具有工程师角色
        validateEngineerRole(engineerId);
        
        // 删除原有的故障类型分配
        engineerFaultRelRepository.deleteByEngineerId(engineerId);
        
        // 创建新的分配关系
        for (Long faultTypeId : faultTypeIds) {
            FaultType faultType = faultTypeRepository.findById(faultTypeId)
                    .orElseThrow(() -> new IllegalArgumentException("故障类型不存在: " + faultTypeId));
            
            if (!engineerFaultRelRepository.existsByEngineerIdAndFaultTypeId(engineerId, faultTypeId)) {
                EngineerFaultRel rel = new EngineerFaultRel(null, engineer, faultType);
                engineerFaultRelRepository.save(rel);
            }
        }
    }
    
    @Override
    public void removeEngineerFromFaultType(Long engineerId, Long faultTypeId) {
        engineerFaultRelRepository.deleteByEngineerIdAndFaultTypeId(engineerId, faultTypeId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<EngineerFaultDTO> getEngineerFaultTypes(Long engineerId) {
        List<EngineerFaultRel> rels = engineerFaultRelRepository.findByEngineerId(engineerId);
        return rels.stream()
                .map(EngineerFaultConverter::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<EngineerFaultDTO> getFaultTypeEngineers(Long faultTypeId) {
        List<EngineerFaultRel> rels = engineerFaultRelRepository.findByFaultTypeId(faultTypeId);
        return rels.stream()
                .map(EngineerFaultConverter::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<EngineerFaultDTO> getAllFaultTypeAssignments() {
        List<EngineerFaultRel> rels = engineerFaultRelRepository.findAll();
        return rels.stream()
                .map(EngineerFaultConverter::toDTO)
                .collect(Collectors.toList());
    }
}
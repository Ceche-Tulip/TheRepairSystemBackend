package org.trs.therepairsystem.service;

import org.trs.therepairsystem.dto.response.engineer.EngineerAreaDTO;
import org.trs.therepairsystem.dto.response.engineer.EngineerFaultDTO;
import org.trs.therepairsystem.dto.response.UserDTO;

import java.util.List;

public interface EngineerAssignService {
    
    /**
     * 获取所有具有工程师角色的用户
     */
    List<UserDTO> getAllEngineers();
    
    // ========== 区域分配管理 ==========
    
    /**
     * 为工程师分配区域（楼层）
     */
    void assignEngineerToAreas(Long engineerId, List<Long> floorIds);
    
    /**
     * 移除工程师的区域分配
     */
    void removeEngineerFromArea(Long engineerId, Long floorId);
    
    /**
     * 查询工程师的所有区域分配
     */
    List<EngineerAreaDTO> getEngineerAreas(Long engineerId);
    
    /**
     * 查询楼层分配给哪些工程师
     */
    List<EngineerAreaDTO> getFloorEngineers(Long floorId);
    
    /**
     * 查询所有区域分配
     */
    List<EngineerAreaDTO> getAllAreaAssignments();
    
    // ========== 故障类型分配管理 ==========
    
    /**
     * 为工程师分配故障类型
     */
    void assignEngineerToFaultTypes(Long engineerId, List<Long> faultTypeIds);
    
    /**
     * 移除工程师的故障类型分配
     */
    void removeEngineerFromFaultType(Long engineerId, Long faultTypeId);
    
    /**
     * 查询工程师的所有故障类型分配
     */
    List<EngineerFaultDTO> getEngineerFaultTypes(Long engineerId);
    
    /**
     * 查询故障类型分配给哪些工程师
     */
    List<EngineerFaultDTO> getFaultTypeEngineers(Long faultTypeId);
    
    /**
     * 查询所有故障类型分配
     */
    List<EngineerFaultDTO> getAllFaultTypeAssignments();
}
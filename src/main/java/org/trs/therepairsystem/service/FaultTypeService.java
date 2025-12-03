package org.trs.therepairsystem.service;

import org.springframework.data.domain.Page;
import org.trs.therepairsystem.entity.FaultType;

import java.util.List;

/**
 * 故障类型业务逻辑接口
 */
public interface FaultTypeService {

    /**
     * 获取所有故障类型
     */
    List<FaultType> getAllFaultTypes();

    /**
     * 分页获取故障类型
     */
    Page<FaultType> getFaultTypes(int page, int size);

    /**
     * 根据ID获取故障类型
     */
    FaultType getFaultTypeById(Long id);

    /**
     * 根据名称查找故障类型
     */
    FaultType getFaultTypeByName(String name);

    /**
     * 创建故障类型
     */
    FaultType createFaultType(FaultType faultType);

    /**
     * 更新故障类型
     */
    FaultType updateFaultType(Long id, FaultType updatedFaultType);

    /**
     * 删除故障类型
     */
    void deleteFaultType(Long id);

    /**
     * 检查故障类型名称是否已存在
     */
    boolean existsByName(String name);

    /**
     * 检查故障类型名称是否已存在（排除指定ID）
     */
    boolean existsByNameAndIdNot(String name, Long id);

    /**
     * 根据名称模糊查询故障类型
     */
    Page<FaultType> searchFaultTypesByName(String name, int page, int size);

    /**
     * 检查故障类型是否可以被删除（未被报修单引用）
     */
    boolean canDelete(Long id);
}
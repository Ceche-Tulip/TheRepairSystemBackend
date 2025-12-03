package org.trs.therepairsystem.service;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.trs.therepairsystem.entity.FaultType;
import org.trs.therepairsystem.repository.FaultTypeRepository;

import java.util.List;

/**
 * 故障类型业务逻辑实现
 */
@Service
@Transactional(readOnly = true)
public class FaultTypeServiceImpl implements FaultTypeService {

    private static final Logger logger = LoggerFactory.getLogger(FaultTypeServiceImpl.class);

    @Autowired
    private FaultTypeRepository faultTypeRepository;

    @Override
    public List<FaultType> getAllFaultTypes() {
        logger.debug("获取所有故障类型");
        return faultTypeRepository.findAllByOrderByName();
    }

    @Override
    public Page<FaultType> getFaultTypes(int page, int size) {
        logger.debug("分页获取故障类型: page={}, size={}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        return faultTypeRepository.findAllByOrderByName(pageable);
    }

    @Override
    public FaultType getFaultTypeById(Long id) {
        logger.debug("根据ID获取故障类型: id={}", id);
        return faultTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("故障类型不存在，ID: " + id));
    }

    @Override
    public FaultType getFaultTypeByName(String name) {
        logger.debug("根据名称获取故障类型: name={}", name);
        return faultTypeRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("故障类型不存在，名称: " + name));
    }

    @Override
    @Transactional
    public FaultType createFaultType(FaultType faultType) {
        logger.info("创建故障类型: {}", faultType.getName());
        
        // 验证参数
        validateFaultType(faultType);
        
        // 检查名称唯一性
        if (faultTypeRepository.existsByName(faultType.getName())) {
            throw new DataIntegrityViolationException("故障类型名称已存在: " + faultType.getName());
        }
        
        try {
            FaultType saved = faultTypeRepository.save(faultType);
            logger.info("故障类型创建成功: id={}, name={}", saved.getId(), saved.getName());
            return saved;
        } catch (Exception e) {
            logger.error("创建故障类型失败: {}", faultType.getName(), e);
            throw new RuntimeException("创建故障类型失败", e);
        }
    }

    @Override
    @Transactional
    public FaultType updateFaultType(Long id, FaultType updatedFaultType) {
        logger.info("更新故障类型: id={}", id);
        
        // 查找现有的故障类型
        FaultType existingFaultType = getFaultTypeById(id);
        
        // 验证更新的数据
        validateFaultType(updatedFaultType);
        
        // 检查名称唯一性（如果名称发生变化）
        if (!existingFaultType.getName().equals(updatedFaultType.getName()) &&
            faultTypeRepository.existsByNameAndIdNot(updatedFaultType.getName(), id)) {
            throw new DataIntegrityViolationException("故障类型名称已存在: " + updatedFaultType.getName());
        }
        
        // 更新字段
        existingFaultType.setName(updatedFaultType.getName());
        existingFaultType.setColor(updatedFaultType.getColor());
        existingFaultType.setDescription(updatedFaultType.getDescription());
        
        try {
            FaultType saved = faultTypeRepository.save(existingFaultType);
            logger.info("故障类型更新成功: id={}, name={}", saved.getId(), saved.getName());
            return saved;
        } catch (Exception e) {
            logger.error("更新故障类型失败: id={}", id, e);
            throw new RuntimeException("更新故障类型失败", e);
        }
    }

    @Override
    @Transactional
    public void deleteFaultType(Long id) {
        logger.info("删除故障类型: id={}", id);
        
        // 检查故障类型是否存在
        FaultType faultType = getFaultTypeById(id);
        
        // 检查是否可以删除（未被报修单引用）
        if (!canDelete(id)) {
            throw new DataIntegrityViolationException("无法删除故障类型，已被报修单引用");
        }
        
        try {
            faultTypeRepository.deleteById(id);
            logger.info("故障类型删除成功: id={}, name={}", id, faultType.getName());
        } catch (Exception e) {
            logger.error("删除故障类型失败: id={}", id, e);
            throw new RuntimeException("删除故障类型失败", e);
        }
    }

    @Override
    public boolean existsByName(String name) {
        return faultTypeRepository.existsByName(name);
    }

    @Override
    public boolean existsByNameAndIdNot(String name, Long id) {
        return faultTypeRepository.existsByNameAndIdNot(name, id);
    }

    @Override
    public Page<FaultType> searchFaultTypesByName(String name, int page, int size) {
        logger.debug("按名称搜索故障类型: name={}, page={}, size={}", name, page, size);
        Pageable pageable = PageRequest.of(page, size);
        return faultTypeRepository.findByNameContaining(name, pageable);
    }

    @Override
    public boolean canDelete(Long id) {
        try {
            // 暂时返回true，等报修单模块创建后再实现具体逻辑
            // return !faultTypeRepository.isReferencedByRepairOrders(id);
            return true;
        } catch (Exception e) {
            // 如果查询失败，为安全起见返回false
            logger.warn("检查故障类型删除权限失败: id={}", id, e);
            return false;
        }
    }

    /**
     * 验证故障类型数据
     */
    private void validateFaultType(FaultType faultType) {
        if (faultType == null) {
            throw new IllegalArgumentException("故障类型不能为空");
        }
        
        if (!StringUtils.hasText(faultType.getName())) {
            throw new IllegalArgumentException("故障类型名称不能为空");
        }
        
        if (faultType.getName().length() > 50) {
            throw new IllegalArgumentException("故障类型名称长度不能超过50字符");
        }
        
        if (!StringUtils.hasText(faultType.getColor())) {
            throw new IllegalArgumentException("故障类型颜色不能为空");
        }
        
        // 验证颜色格式（十六进制颜色代码）
        if (!faultType.getColor().matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")) {
            throw new IllegalArgumentException("故障类型颜色格式不正确，应为十六进制颜色代码");
        }
        
        if (faultType.getDescription() != null && faultType.getDescription().length() > 200) {
            throw new IllegalArgumentException("故障类型描述长度不能超过200字符");
        }
    }
}
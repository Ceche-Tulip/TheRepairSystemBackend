package org.trs.therepairsystem.web.converter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.trs.therepairsystem.entity.FaultType;
import org.trs.therepairsystem.dto.request.faulttype.FaultTypeCreateRequest;
import org.trs.therepairsystem.dto.response.FaultTypeDTO;
import org.trs.therepairsystem.dto.request.faulttype.FaultTypeUpdateRequest;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 故障类型转换器
 * 负责Entity和DTO之间的转换
 */
public class FaultTypeConverter {

    /**
     * 将FaultType实体转换为FaultTypeDTO
     */
    public static FaultTypeDTO toDTO(FaultType faultType) {
        if (faultType == null) {
            return null;
        }
        return new FaultTypeDTO(
                faultType.getId(),
                faultType.getName(),
                faultType.getColor(),
                faultType.getDescription()
        );
    }

    /**
     * 将FaultType实体列表转换为FaultTypeDTO列表
     */
    public static List<FaultTypeDTO> toDTOList(List<FaultType> faultTypes) {
        if (faultTypes == null) {
            return null;
        }
        return faultTypes.stream()
                .map(FaultTypeConverter::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 将FaultType分页对象转换为FaultTypeDTO分页对象
     */
    public static Page<FaultTypeDTO> toDTOPage(Page<FaultType> faultTypePage) {
        if (faultTypePage == null) {
            return null;
        }
        List<FaultTypeDTO> dtoList = faultTypePage.getContent().stream()
                .map(FaultTypeConverter::toDTO)
                .collect(Collectors.toList());
        
        return new PageImpl<>(dtoList, faultTypePage.getPageable(), faultTypePage.getTotalElements());
    }

    /**
     * 将FaultTypeCreateRequest转换为FaultType实体
     */
    public static FaultType fromCreateRequest(FaultTypeCreateRequest request) {
        if (request == null) {
            return null;
        }
        FaultType faultType = new FaultType();
        faultType.setName(request.getName());
        faultType.setColor(request.getColor());
        faultType.setDescription(request.getDescription());
        return faultType;
    }

    /**
     * 用FaultTypeUpdateRequest更新FaultType实体
     * 只更新非null的字段
     */
    public static void updateFromRequest(FaultType faultType, FaultTypeUpdateRequest request) {
        if (faultType == null || request == null) {
            return;
        }
        
        if (request.getName() != null) {
            faultType.setName(request.getName());
        }
        if (request.getColor() != null) {
            faultType.setColor(request.getColor());
        }
        if (request.getDescription() != null) {
            faultType.setDescription(request.getDescription());
        }
    }
}
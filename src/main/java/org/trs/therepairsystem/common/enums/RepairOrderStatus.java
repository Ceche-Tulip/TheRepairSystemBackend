package org.trs.therepairsystem.common.enums;

/**
 * 报修单状态枚举
 * 简化流程：用户提交 → 待处理 → 处理中 → 已完成 → 已关闭
 */
public enum RepairOrderStatus {
    
    /**
     * 待提交 - 用户编辑中，尚未提交
     */
    DRAFT(0, "待提交"),
    
    /**
     * 已提交 - 用户已提交，等待分配工程师
     */
    SUBMITTED(1, "已提交"),
    
    /**
     * 待处理 - 已分配工程师，等待工程师接单
     */
    PENDING(2, "待处理"),
    
    /**
     * 处理中 - 工程师已接单，正在维修
     */
    IN_PROGRESS(3, "处理中"),
    
    /**
     * 已完成 - 工程师维修完成，等待用户确认
     */
    COMPLETED(4, "已完成"),
    
    /**
     * 已关闭 - 用户确认完成，流程结束
     */
    CLOSED(5, "已关闭"),
    
    /**
     * 已撤销 - 用户主动撤销
     */
    CANCELLED(-1, "已撤销");
    
    private final Integer code;
    private final String description;
    
    RepairOrderStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据code获取枚举
     */
    public static RepairOrderStatus fromCode(Integer code) {
        for (RepairOrderStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的报修单状态代码: " + code);
    }
    
    /**
     * 判断是否可以撤销
     */
    public boolean canCancel() {
        return this == DRAFT || this == PENDING;
    }
    
    /**
     * 判断是否可以接单
     */
    public boolean canAccept() {
        return this == PENDING;
    }
    
    /**
     * 判断是否可以完成
     */
    public boolean canComplete() {
        return this == IN_PROGRESS;
    }
    
    /**
     * 判断是否可以关闭
     */
    public boolean canClose() {
        return this == COMPLETED;
    }
    
    /**
     * 判断是否为最终状态
     */
    public boolean isFinalStatus() {
        return this == CLOSED || this == CANCELLED;
    }
}
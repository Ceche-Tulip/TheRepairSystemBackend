package org.trs.therepairsystem.common.enums;

/**
 * 公告状态枚举
 */
public enum AnnouncementStatus {
    
    /**
     * 草稿状态 - 管理员创建但未发布
     */
    DRAFT(0, "草稿"),
    
    /**
     * 已发布 - 正式发布，用户可见
     */
    PUBLISHED(1, "已发布"),
    
    /**
     * 已下架 - 暂时下架，用户不可见
     */
    UNPUBLISHED(2, "已下架"),
    
    /**
     * 已删除 - 软删除状态
     */
    DELETED(3, "已删除");
    
    private final Integer code;
    private final String description;
    
    AnnouncementStatus(Integer code, String description) {
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
    public static AnnouncementStatus fromCode(Integer code) {
        for (AnnouncementStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的公告状态代码: " + code);
    }
    
    /**
     * 判断是否为用户可见状态
     */
    public boolean isVisible() {
        return this == PUBLISHED;
    }
    
    /**
     * 判断是否为已删除状态
     */
    public boolean isDeleted() {
        return this == DELETED;
    }
}
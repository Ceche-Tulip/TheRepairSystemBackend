package org.trs.therepairsystem.entity;

import org.trs.therepairsystem.common.enums.RepairOrderStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "repair_order", indexes = {
    @Index(name = "idx_user_status", columnList = "user_id, status"),
    @Index(name = "idx_engineer_status", columnList = "engineer_id, status"),
    @Index(name = "idx_status_create_time", columnList = "status, create_time"),
    @Index(name = "idx_building_floor", columnList = "building_id, floor_id")
})
public class RepairOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 提交用户
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User submitUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id", nullable = false)
    private Floor floor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fault_type_id", nullable = false)
    private FaultType faultType;

    // 管理员（可为空）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private User admin;

    // 分配的工程师（可为空）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "engineer_id")
    private User engineer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RepairOrderStatus status;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String repairInfo;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "accept_time")
    private LocalDateTime acceptTime;

    @Column(name = "finish_time")
    private LocalDateTime finishTime;

    @PrePersist
    protected void onCreate() {
        if (createTime == null) {
            createTime = LocalDateTime.now();
        }
        if (status == null) {
            status = RepairOrderStatus.PENDING; // 用户提交后直接进入待处理状态
        }
    }

    @PreUpdate
    protected void onUpdate() {
        // 状态变更时自动设置时间
        if (status == RepairOrderStatus.IN_PROGRESS && acceptTime == null) {
            acceptTime = LocalDateTime.now();
        }
        if ((status == RepairOrderStatus.COMPLETED || status == RepairOrderStatus.CLOSED) && finishTime == null) {
            finishTime = LocalDateTime.now();
        }
    }

    // 业务方法
    public boolean canAssignEngineer() {
        return status == RepairOrderStatus.PENDING || status == RepairOrderStatus.IN_PROGRESS;
    }

    public boolean canAccept() {
        return status == RepairOrderStatus.PENDING;
    }

    public boolean canComplete() {
        return status == RepairOrderStatus.IN_PROGRESS;
    }

    public boolean canCancel() {
        return status == RepairOrderStatus.DRAFT || status == RepairOrderStatus.PENDING;
    }

    public boolean isAssignedTo(Long engineerId) {
        return engineer != null && engineer.getId().equals(engineerId);
    }

    public boolean isSubmittedBy(Long userId) {
        return submitUser.getId().equals(userId);
    }
}
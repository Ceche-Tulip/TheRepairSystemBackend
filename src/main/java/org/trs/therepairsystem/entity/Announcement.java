package org.trs.therepairsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import org.trs.therepairsystem.common.enums.AnnouncementStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "announcement")
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    @Column(name = "is_top", nullable = false)
    private Boolean isTop = false;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private AnnouncementStatus status;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createTime = now;
        updateTime = now;
        
        // 设置默认状态为草稿
        if (status == null) {
            status = AnnouncementStatus.DRAFT;
        }
        
        // 设置默认不置顶
        if (isTop == null) {
            isTop = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
package org.trs.therepairsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "repair_rating", indexes = {
    @Index(name = "idx_order_id", columnList = "order_id"),
    @Index(name = "idx_rating", columnList = "rating")
})
public class RepairRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private RepairOrder order;

    @Column(nullable = false)
    private Integer rating; // 1-5星评价

    @Column(length = 500)
    private String comment; // 增加长度到500

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        if (createTime == null) {
            createTime = LocalDateTime.now();
        }
    }

    // 业务方法
    public boolean isValidRating() {
        return rating != null && rating >= 1 && rating <= 5;
    }
}
package org.trs.therepairsystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "repair_order")
public class RepairOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // who submitted
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User submitUser;

    @ManyToOne
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    @ManyToOne
    @JoinColumn(name = "floor_id", nullable = false)
    private Floor floor;

    @ManyToOne
    @JoinColumn(name = "fault_type_id", nullable = false)
    private FaultType faultType;

    // admin assigned (nullable)
    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User admin;

    // engineer assigned (nullable)
    @ManyToOne
    @JoinColumn(name = "engineer_id")
    private User engineer;

    @Column(nullable = false)
    private Integer status;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String repairInfo;

    private java.time.LocalDateTime createTime;
    private java.time.LocalDateTime acceptTime;
    private java.time.LocalDateTime finishTime;
}
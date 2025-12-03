package org.trs.therepairsystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "engineer_fault_rel")
public class EngineerFaultRel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "engineer_id", nullable = false)
    private User engineer;

    @ManyToOne
    @JoinColumn(name = "fault_type_id", nullable = false)
    private FaultType faultType;
}
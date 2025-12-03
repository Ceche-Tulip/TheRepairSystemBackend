package org.trs.therepairsystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "engineer_area_rel")
public class EngineerAreaRel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "engineer_id", nullable = false)
    private User engineer;

    @ManyToOne
    @JoinColumn(name = "floor_id", nullable = false)
    private Floor floor;
}
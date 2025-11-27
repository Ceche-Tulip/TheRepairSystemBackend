package org.trs.therepairsystem.pojo;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "fault_type",
        uniqueConstraints = @UniqueConstraint(columnNames = "name")
)
public class FaultType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 20)
    private String color;

    @Column(columnDefinition = "TEXT")
    private String description;
}

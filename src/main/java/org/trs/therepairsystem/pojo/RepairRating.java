package org.trs.therepairsystem.pojo;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "repair_rating")
public class RepairRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private RepairOrder order;

    @Column(nullable = false)
    private Integer rating;

    @Column(length = 200)
    private String comment;
}

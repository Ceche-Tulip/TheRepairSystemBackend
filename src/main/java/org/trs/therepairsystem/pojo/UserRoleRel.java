package org.trs.therepairsystem.pojo;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_role_rel")
public class UserRoleRel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many role relations belong to one User
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Many role relations belong to one Role
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private UserRole role;
}

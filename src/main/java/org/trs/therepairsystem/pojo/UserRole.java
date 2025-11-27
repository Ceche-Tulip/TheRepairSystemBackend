package org.trs.therepairsystem.pojo;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "user_role",  // 配置表名
        uniqueConstraints = @UniqueConstraint(columnNames = "role_name")  // 配置不可重复字段
)
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "role_name", nullable = false, length = 50)
    private String roleName;
}

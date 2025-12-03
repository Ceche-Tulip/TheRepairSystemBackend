package org.trs.therepairsystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// 所有的pojo类都使用如下类似的注解配置
@Data  // 自动生成getter、setter等方法
@NoArgsConstructor  // 生成无参构造函数
@AllArgsConstructor  // 生成全参构造函数
@Entity  // 指定为JPA实体
@Table(
        name = "users",  // 配置表名
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"username"}),  // 配置不可重复字段
                @UniqueConstraint(columnNames = {"phone"})
        }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 50)
    private String realName;

    @Column(nullable = false, length = 20)
    private String phone;
}
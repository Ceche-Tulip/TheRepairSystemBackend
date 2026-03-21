# TheRepairSystem - 维修工单系统后端开发总结

## 项目概述

TheRepairSystem 是一个基于 Spring Boot 3.3.5 的维修工单管理系统后端服务，支持用户报修、工程师接单、管理员管理等完整的维修工单流程。

## 技术栈

- 框架: Spring Boot 3.3.5
- 数据库: MySQL + JPA/Hibernate
- 安全: Spring Security + JWT
- 文档: Swagger/OpenAPI 3
- 构建工具: Maven
- Java版本: JDK 21

## 核心功能模块

### 1. 用户管理模块
- 用户注册/登录: JWT token 认证
- 角色权限: 支持普通用户、工程师、管理员三种角色
- 用户信息管理: 基本信息维护

### 2. 维修工单模块
- 工单生命周期: DRAFT -> PENDING -> IN_PROGRESS -> COMPLETED -> CLOSED
- 用户功能:
  - 提交维修申请
  - 保存草稿/提交草稿
  - 查看工单状态
  - 关闭工单并评价
- 工程师功能:
  - 接收工单
  - 完成维修工作
  - 查看个人工单
- 管理员功能:
  - 分配工程师
  - 查看所有工单
  - 工单统计

### 3. 评价系统模块
- 评价提交: 用户对完成的工单进行 1-5 星评价
- 评价查询: 支持按工单、工程师、用户等维度查询
- 统计功能: 工程师评价统计、平均分计算

### 4. 基础数据模块
- 建筑楼层管理: 支持多建筑、多楼层
- 故障类型管理: 可配置的故障分类
- 工程师技能配置: 工程师擅长的故障类型和负责区域

### 5. 权限安全模块
- JWT 认证: 无状态 token 认证
- 角色权限控制: 基于角色的访问控制 (RBAC)
- 接口权限: 细粒度 API 权限控制

## 项目目录结构

```text
src/
├── main/
│   ├── java/org/trs/therepairsystem/
│   │   ├── TheRepairSystemApplication.java           # 应用启动类
│   │   ├── common/                                   # 通用模块
│   │   │   ├── dto/
│   │   │   │   └── ApiResponse.java                 # 统一响应格式
│   │   │   ├── enums/
│   │   │   │   └── RepairOrderStatus.java           # 工单状态枚举
│   │   │   └── exception/
│   │   │       ├── BusinessException.java           # 业务异常
│   │   │       └── GlobalExceptionHandler.java      # 全局异常处理
│   │   ├── config/                                   # 配置类
│   │   │   ├── SwaggerConfig.java                   # Swagger文档配置
│   │   │   └── WebMvcConfig.java                    # Web配置
│   │   ├── controller/                               # 控制器层
│   │   │   ├── RepairOrderController.java           # 维修工单接口
│   │   │   ├── RepairRatingController.java          # 评价管理接口
│   │   │   ├── RoleController.java                  # 角色管理接口
│   │   │   └── UserController.java                  # 用户管理接口
│   │   ├── dto/                                     # 数据传输对象
│   │   │   ├── request/                             # 请求DTO
│   │   │   │   ├── RepairOrderSubmitRequest.java    # 工单提交请求
│   │   │   │   ├── RepairOrderAssignRequest.java    # 工单分配请求
│   │   │   │   ├── RepairOrderCompleteRequest.java  # 工单完成请求
│   │   │   │   └── RepairRatingRequest.java         # 评价请求
│   │   │   └── response/                            # 响应DTO
│   │   │       ├── RepairOrderResponse.java         # 工单响应
│   │   │       ├── RepairRatingResponse.java        # 评价响应
│   │   │       └── EngineerResponse.java            # 工程师响应
│   │   ├── entity/                                  # 实体类
│   │   │   ├── User.java                           # 用户实体
│   │   │   ├── UserRole.java                       # 角色实体
│   │   │   ├── UserRoleRel.java                    # 用户角色关联
│   │   │   ├── RepairOrder.java                    # 维修工单实体
│   │   │   ├── RepairRating.java                   # 维修评价实体
│   │   │   ├── Building.java                       # 建筑实体
│   │   │   ├── Floor.java                          # 楼层实体
│   │   │   ├── FaultType.java                      # 故障类型实体
│   │   │   ├── EngineerAreaRel.java                # 工程师区域关联
│   │   │   └── EngineerFaultRel.java               # 工程师故障类型关联
│   │   ├── repository/                              # 数据访问层
│   │   │   ├── UserRepository.java                 # 用户数据访问
│   │   │   ├── RoleRepository.java                 # 角色数据访问
│   │   │   ├── UserRoleRelRepository.java          # 用户角色关联数据访问
│   │   │   ├── RepairOrderRepository.java          # 工单数据访问
│   │   │   └── RepairRatingRepository.java         # 评价数据访问
│   │   ├── security/                                # 安全模块
│   │   │   ├── SecurityConfig.java                 # Spring Security配置
│   │   │   ├── JwtAuthenticationFilter.java        # JWT认证过滤器
│   │   │   ├── JwtUtil.java                        # JWT工具类
│   │   │   ├── CustomUserDetails.java              # 自定义用户详情
│   │   │   └── CustomUserDetailsService.java       # 用户详情服务
│   │   ├── service/                                 # 业务逻辑层
│   │   │   ├── RepairOrderService.java             # 工单服务接口
│   │   │   ├── RepairOrderServiceImpl.java         # 工单服务实现
│   │   │   ├── RepairRatingService.java            # 评价服务接口
│   │   │   ├── RepairRatingServiceImpl.java        # 评价服务实现
│   │   │   ├── RoleService.java                    # 角色服务接口
│   │   │   ├── RoleServiceImpl.java                # 角色服务实现
│   │   │   ├── UserService.java                    # 用户服务接口
│   │   │   └── UserServiceImpl.java                # 用户服务实现
│   │   └── web/                                     # Web相关
│   │       └── AuthController.java                 # 认证控制器
│   └── resources/
│       └── application.yml                          # 应用配置文件
└── test/                                           # 测试模块
    └── java/org/trs/therepairsystem/
        ├── TheRepairSystemApplicationTests.java    # 应用测试
        ├── UserRepositoryTest.java                 # 用户数据访问测试
        └── controller/                             # 控制器测试
```

## 密码加密说明（重要）

- 当前后端密码算法为 BCrypt。
- 在 SecurityConfig 中通过 new BCryptPasswordEncoder() 配置。
- BCrypt 是单向哈希，不可逆，不能从密文还原明文。
- 同一个明文每次加密结果都可能不同，这是正常现象（随机盐）。

### 生成与验证 BCrypt 密文

项目已提供工具类:

- 文件: src/test/java/org/trs/UseBCrypt.java
- 作用:
  - 传 1 个参数: 生成密文
  - 传 2 个参数: 验证明文与密文是否匹配

示例:

```bash
# 生成 admin 的 BCrypt 密文
.\mvnw -q -DskipTests test-compile exec:java -Dexec.mainClass=org.trs.UseBCrypt -Dexec.args="admin"

# 验证 admin 是否匹配某个密文
.\mvnw -q -DskipTests test-compile exec:java -Dexec.mainClass=org.trs.UseBCrypt -Dexec.args="admin $2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
```

说明: 如果你本地没有配置 exec-maven-plugin，也可以在 IDE 中直接运行 UseBCrypt.main。

### 密码相关接口（Swagger 可见）

- `POST /api/users/me/password`: 当前登录用户修改自己的密码（需要 `oldPassword` 和 `newPassword`）。
- `POST /api/users/{id}/password/reset`: 管理员重置指定用户密码（仅 `ADMIN` 可调用，需要 `newPassword`）。

## 初始化管理员账号（SQL 示例）

请将 users.password 填写为 BCrypt 密文，而不是明文。

```sql
USE therepairsystem;

-- 第一步：插入三个基础角色
INSERT INTO user_role (id, role_name) VALUES
(1, 'USER'),
(2, 'ENGINEER'),
(3, 'ADMIN');

-- 第二步：插入默认管理员用户
-- password 必须是对明文 admin 的 BCrypt 密文（可用 UseBCrypt 生成）
INSERT INTO users (id, username, password, phone, real_name) VALUES
(1, 'admin', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.jqstwCa', '13800000000', '系统管理员');

-- 第三步：绑定 ADMIN 角色
INSERT INTO user_role_rel (role_id, user_id) VALUES
(3, 1);
```

## 开发与运行

### 编译

```bash
# 使用当前项目的 Maven Wrapper 进行编译
.\mvnw compile

# 清除缓存并重新编译
.\mvnw clean compile

# 需要详细错误信息时
.\mvnw clean compile -e
```

### 启动

```bash
.\mvnw spring-boot:run
```

### 访问地址
- API 文档: http://localhost:8080/swagger-ui/index.html#/
- 应用端点: http://localhost:8080/api/*

## 总结

本项目已实现维修工单系统后端的核心能力，具备清晰分层架构、可扩展的权限体系和完整的工单流程，适合作为进一步迭代的基础框架。

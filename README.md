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

### AI 接口（Swagger 已统一）

- `POST /api/ai/ask`: AI 提问接口，文档注解风格已与其他控制器统一（Tag/Operation/ApiResponses）。

### 工单附件接口（新增）

- `POST /api/repair-orders/{orderId}/attachments`: 上传附件（multipart/form-data，字段：`file`、`attachmentType`）。
- `GET /api/repair-orders/{orderId}/attachments`: 获取工单附件列表。
- `GET /api/repair-orders/{orderId}/attachments/{attachmentId}/download`: 下载附件。
- `DELETE /api/repair-orders/{orderId}/attachments/{attachmentId}`: 删除附件。

权限规则：
- 上传/查询/下载：工单提交人、当前分配工程师、管理员。
- 删除：附件上传者本人或管理员。

约束规则：
- 一个工单可有多个附件，也可没有附件。
- 仅支持 `image/jpeg`、`image/png`、`image/webp`。
- 默认单文件大小限制为 10MB。

## 数据库初始化（seed.sql）

项目**不带任何自动数据初始化**：仓库里没有 `data.sql` / `import.sql`，也没有 `CommandLineRunner`
之类的启动钩子，Hibernate 的 `ddl-auto: update` 只负责建表、不插数据。

所以全新部署后必须额外导入一份初始数据，否则：

- `users` 表为空 → 无法登录；
- 楼栋 / 楼层 / 故障类型为空 → 报修页的下拉框全空，工单提交不了；
- 没有工程师及其技能 / 区域配置 → 自动分配无从匹配。

仓库根目录的 `seed.sql` 一次性解决这些，内容如下：

| 内容 | 条数 |
| --- | --- |
| 角色（USER / ENGINEER / ADMIN） | 3 |
| 账号（1 管理员 + 2 工程师 + 2 普通用户） | 5 |
| 楼栋 / 楼层 | 10 / 38 |
| 故障类型 | 10 |
| 工程师技能（可处理的故障类型） | 9 |
| 工程师负责区域 | 19 |

### 初始化步骤

```bash
# 1) 建库（必须指定 utf8mb4，否则中文会乱码）
mysql -uroot -p -e "CREATE DATABASE therepairsystem DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;"

# 2) 先启动一次后端，等 Hibernate 把 12 张表建好
#    （日志出现 "Started TheRepairSystemApplication" 即可）

# 3) 停掉后端，导入初始数据
mysql -uroot -p --default-character-set=utf8mb4 therepairsystem < seed.sql
```

`seed.sql` 全部使用 `INSERT IGNORE` + 显式主键，**可以重复执行**，不会报错、也不会覆盖已有数据。
脚本末尾带一条自检 SELECT，会打印各表实际行数，便于确认导入成功（全新环境 `repair_order` 应为 0）。

### 预置账号

三个角色对应的账号如下，密码统一为演示密码 —— 明文写在 `seed.sql` 的文件头注释里。
`password` 字段存的是 BCrypt 密文，同一明文每次加密结果都不同，属正常现象。

| 角色 | 账号 | 姓名 |
| --- | --- | --- |
| 系统管理员 | `admin` | 系统管理员 |
| 工程师 | `engineer1` / `engineer2` | 张建国 / 李文博 |
| 普通用户 | `student1` / `student2` | 王小明 / 刘芳 |

> 说明：`seed.sql` 只预置账号与基础字典，**不预置报修单和评价**，全新环境工单数为 0，
> 便于完整演示「提交报修 → 自动分配 → 接单 → 结单 → 评价」的整条流程。
> 另外，工程师的区域覆盖是演示用的子集：楼层 15-18、24-38 以及「电梯故障」没有工程师负责，
> 这类工单会自动分配失败并落到「管理员手动分配」路径，正好用来演示手动指派。
> 若要覆盖全部区域，在 `engineer_area_rel` 里补对应关系即可。

### 只想建一个管理员时

最小可用集是「3 个角色 + 1 个管理员」，即 `seed.sql` 的第 1、2、3 节，可单独执行。
若手工插入，`password` 必须是 BCrypt 密文（**不能写明文**），可用项目自带的工具生成：

```bash
.\mvnw -q -DskipTests test-compile exec:java -Dexec.mainClass=org.trs.UseBCrypt -Dexec.args="你的明文密码"
```

## 开发与运行

### 快速开始（5 步）

```text
1. 装好 JDK 21 + MySQL 8.0，建库：therepairsystem（utf8mb4）
2. 复制 src/main/resources/application-local.yml.example → application-local.yml，填数据库账号密码（可顺带填 AI Key）
3. .\mvnw spring-boot:run 启动一次，等日志出现 "Started TheRepairSystemApplication"（此时 Hibernate 已建好表）
4. 停掉服务，执行 seed.sql 导入预置账号与基础数据
5. 重新启动，前端用 admin / engineer1 / student1 登录
```

> 第 3、4 步的顺序不能颠倒：建表由 Hibernate 负责，`seed.sql` 只插数据。

### 环境要求

| 组件 | 版本 | 说明 |
| --- | --- | --- |
| JDK | **21** | `pom.xml` 中 `java.version=21`、compiler source/target 均为 21，低于 21 无法编译 |
| MySQL | 8.0+ | 字符集 utf8mb4、引擎 InnoDB；与后端同机部署时无需开启远程访问 |
| Maven | — | 无需单独安装，用项目自带的 `mvnw.cmd`（Windows）/ `mvnw`（Linux、macOS） |
| Docker | 可选 | 仅用于跑 MinIO；不用 MinIO 时附件存本地磁盘 |

前端不在本仓库，需单独部署：开发环境 `npm run serve`（默认 9090 端口，代理 `/api` 到 8080），
生产环境 `npm run build` 后用 Nginx 托管 `dist`。

### MinIO 部署与配置（Docker）

以下流程用于第二轮附件存储接入（本地存储与 MinIO 可切换）：

#### 1) 使用 Docker Compose 启动 MinIO

项目根目录已提供 `docker-compose.yml`，可直接执行：

```bash
docker compose up -d
```

查看运行状态：

```bash
docker compose ps
docker logs --tail 50 repair-minio
```

停止与清理（保留数据卷目录 `minio_data`）：

```bash
docker compose down
```

说明：
- MinIO 要求密码至少 8 位，`root/root` 无法启动；遂使用 `root/root12345`。
- `minio_data` 映射在项目目录下，仅是本地运行数据，不会进入 Git（已在 `.gitignore` 忽略）。
- 容器运行在 Docker Desktop 的 Linux VM 中，通过端口映射暴露到你本机 `localhost`。

启动后访问：
- MinIO API: `http://localhost:9000`
- MinIO Console: `http://localhost:9001`（账号/密码：`root/root12345`）

#### 2) 准备 bucket

项目默认 bucket 为 `repair-attachments`，并支持自动创建（`auto-create-bucket: true`）。

如果你希望手动创建，也可以在 MinIO Console 中新建同名 bucket。

#### 3) 在本地配置中切换为 MinIO

先从模板复制：

```bash
Copy-Item src/main/resources/application-local.yml.example src/main/resources/application-local.yml
```

然后编辑 `src/main/resources/application-local.yml`：

```yaml
file:
  upload:
    storage-provider: MINIO
    base-path: uploads
    max-file-size-bytes: 10485760
    allowed-content-types: image/jpeg,image/png,image/webp
    minio:
      enabled: true
      endpoint: http://localhost:9000
      access-key: root
      secret-key: root12345
      bucket: repair-attachments
      auto-create-bucket: true
```

说明：
- 当 `storage-provider: LOCAL` 时，继续使用本地文件系统。
- 当 `storage-provider: MINIO` 时，上传走 MinIO；历史附件会按数据库中记录的 `storageProvider` 自动路由读取。

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

### 打包

`Dockerfile` 用的是 `COPY target/*.jar`，所以用 Docker 部署前**必须先打包**，否则 `docker build` 会失败：

```bash
.\mvnw clean package -DskipTests
# 产物：target/TheRepairSystem-0.0.1-SNAPSHOT.jar
```

也可以不经过 Docker，直接用 Fat Jar 启动：

```bash
java -jar target/TheRepairSystem-0.0.1-SNAPSHOT.jar
```

### AI 服务配置

AI 问答走 SiliconFlow，密钥放在 `application-local.yml`（已在 `.gitignore` 中忽略，不会入库）：

```yaml
siliconflow:
  api-url: https://api.siliconflow.cn/v1/chat/completions
  api-key: 你的 SiliconFlow API Key
  model: deepseek-ai/DeepSeek-V3.2
```

不填写时其余功能不受影响，只有 `POST /api/ai/ask`（AI 小助手）会报错。

### 访问地址
- API 文档: http://localhost:8080/swagger-ui/index.html#/
- 应用端点: http://localhost:8080/api/*

## 总结

本项目已实现维修工单系统后端的核心能力，具备清晰分层架构、可扩展的权限体系和完整的工单流程，适合作为进一步迭代的基础框架。

# SpringBoot + MyBatis-Plus 用户管理系统模板 📦

## 简介 💡

**这是一个基于 Spring Boot + MyBatis-Plus 的轻量级用户管理系统模板**，提供常见 CRUD、分页、统一响应封装、枚举工具、JWT 登录鉴权、RBAC 权限管理、以及代码生成器示例。适合新项目起步或学习参考。

---

## 主要特性 ✅

- 基于 Spring Boot 3.5.5（Java 17）
- MyBatis-Plus 集成（分页、自动填充、代码生成）
- JWT 登录鉴权，Spring Security 权限控制（RBAC）
- 统一 API 响应封装（`ApiResponse`），可用 `@NoApiWrap` 跳过
- 内置代码生成器（`src/main/java/devtools/CodeGenerator.java` + Freemarker 模板）
- Swagger / OpenAPI 文档（springdoc）

---



## 技术栈与依赖分组 🔧

- **Java 17**
- **Spring Boot 3.5.5**
   - Web 支持：`spring-boot-starter-web`
   - 安全框架：`spring-boot-starter-security`、`spring-security-crypto`
- **MyBatis-Plus 3.5.5**
   - 启动器：`mybatis-plus-spring-boot3-starter`
   - 代码生成器：`mybatis-plus-generator`
- **MySQL 8.3.0**（建表 SQL 在 `init.sql`，驱动：`mysql-connector-j`）
- **JWT 认证**（`io.jsonwebtoken`）
   - `jjwt-api`（主包）、`jjwt-impl`（运行时）、`jjwt-jackson`（运行时，支持 Jackson 序列化）
- **文档与开发工具**
   - OpenAPI 文档：`springdoc-openapi-starter-webmvc-ui`
   - Lombok：`lombok`（仅开发期依赖）
   - Freemarker：`freemarker`（代码生成模板在 `src/main/resources/templates/`）

> 依赖已在 `pom.xml` 中分组并添加详细注释，便于维护和理解。
> 
> **依赖维护建议：**
> - `pom.xml` 依赖已分为 Web/REST、安全、数据库、JWT、开发工具等分组，并为每个依赖添加了中文注释。
> - 推荐后续新增依赖时，按分组和注释风格补充，保持结构清晰。
> - 依赖版本管理建议统一在 `<dependency>` 内指定，便于升级和排查。
> - 如需了解依赖分组和注释示例，请直接参考 `pom.xml` 文件。

---

## 目录结构（关键文件）

主包路径：`src/main/java/com/github/zxs1994/java_template/`
   - `controller/`：用户、角色、权限等 REST 控制器（如 `UserController`、`RoleController` 等）
   - `entity/`：实体类（如 `User`、`Role`、`Permission` 等）
   - `service/`：业务接口与实现（如 `IUserService`、`UserServiceImpl`）
   - `mapper/`：MyBatis-Plus Mapper 接口
   - `config/`：配置类（如 `SecurityConfig`、`JwtAuthenticationFilter`、`MyBatisPlusConfig`）
   - `common/`：通用响应、异常、基础类（如 `ApiResponse`、`BaseEntity`、`BizException`）
   - `util/`：工具类（如 `EnumUtils`、`TimeProvider`、`JwtUtils`、`LoadYaml`）
   - `dto/`：数据传输对象（如 `LoginRequest`、`LoginResponse`）
   - `enums/`：枚举类型
   - `devtools/`：代码生成器入口（`src/main/java/devtools/CodeGenerator.java`）
资源文件：
   - `src/main/resources/application.yml`、`application-dev.yml`、`application-prod.yml`、`project.yml`：配置文件（全部为 yml 格式）
   - `src/main/resources/templates/`：代码生成 Freemarker 模板（entity、controller）
数据库建表 SQL：
   - `init.sql`：包含 user、role、permission、user_role、role_permission 五张表结构

---

## 快速开始 🚀

### 前置条件
- JDK 17  
- Maven  
- MySQL（或修改 `application-dev.yml` 为你的数据源）

### 克隆 & 构建
```bash
git clone <repo-url>
cd java_template
mvn clean package
```

### 运行
- 开发（使用 dev 配置）
```bash
mvn spring-boot:run
java -jar target/java_template-1.0.0.jar
```

- 生产运行示例（带 JVM 时区参数，见 `deploy.sh`）：
```bash
# 最简单启动（示例）
java -jar target/java_template-1.0.0.jar --spring.profiles.active=prod

# 带示例 JVM 内存配置（可选）
java -Xms512m -Xmx1g -jar target/java_template-1.0.0.jar --spring.profiles.active=prod
```


### 配置
- 默认激活 profile：`application.yml` 中 `spring.profiles.active=dev`
- 开发环境数据库配置：`src/main/resources/application-dev.yml`（示例已指向 `jdbc:mysql://127.0.0.1:3306/demo?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false`）

---

## 数据库 & 样例数据 🗄️

数据库建表与样例数据在 `init.sql`，包含 user、role、permission、user_role、role_permission 五张表结构。导入后即可直接测试 API。

---

## API 示例（重要端点） 🔎

- 用户相关：
   - 列表：GET /user
   - 获取：GET /user/{id}
   - 新增：POST /user  （JSON body）
   - 更新：PUT /user/{id}   （JSON body）
   - 删除：DELETE /user/{id}
   - 分页：GET /user/page?page=1&size=10
- 枚举统一接口：GET /enums/all
- 角色、权限、用户-角色、角色-权限等接口均有对应 CRUD

---


## 权限管理（RBAC） 🔐

**概述**：
   - 新增基于角色-权限的权限管理，主要数据库表为 `sys__permission`（权限）和 `sys__role_permission`（角色-权限关联）。
   - **权限表无需手动维护，所有变更均由扫描器自动完成，手动更改会被覆盖或逻辑删除。**
   - 支持通过启动时扫描 Controller 自动同步权限到数据库（可选）。

**自动扫描与初始化**：
   - `SysPermissionScanner`（仅在 `dev` profile 生效，类上有 `@Profile("dev")`）可在启动时扫描 `@RestController` 的接口并同步到 `sys__permission`。默认不开启，配置项：
      - `sys-permission.scan-on-startup: false`（位于 `application-dev.yml`）
   - 项目会初始化一组**全局权限**：`ALL`, `ALL_GET`, `ALL_POST`, `ALL_PUT`, `ALL_DELETE`，用于快速控制全局访问。
   - **路由自动生成：** 数据库表名如 `sys__user` 会自动生成 `/sys/user` 路由，`__` 是分隔约定，表注释如 `COMMENT='系统--用户表'` 也会自动用于接口分组和文档。

**白名单 & 授权判断**：
   - 白名单通过 `security.permit-urls` 配置（`SecurityProperties`）配置路径模式（支持 Ant 风格），示例在 `application-dev.yml` 中可配置。
   - 权限校验流程（`SysPermissionFilter`）：
      1. 首先匹配白名单（若匹配则放行，白名单接口不加锁，文档和分组也会完整展示）。
      2. 检查登录状态（未登录返回 401）。
      3. 查询用户拥有的权限并按 HTTP 方法与路径进行匹配（匹配则放行，否则返回 403）。

**相关 API（权限）**：
   - 权限列表：GET `/sys/permission`
   - 获取权限：GET `/sys/permission/{id}`
   - 新增权限：POST `/sys/permission` （JSON body）
   - 更新权限：PUT `/sys/permission/{id}` （JSON body）
   - 删除权限：DELETE `/sys/permission/{id}`
   - 分页：GET `/sys/permission/page?page=1&size=10`

**使用建议**：
   - 开发时可临时打开 `sys-permission.scan-on-startup: true` 来初始化或同步权限，**慎用**（可能覆盖已有权限数据）。
   - 扫描会排除 Swagger/OpenAPI 相关 Controller（类名包含 `swagger` 或 `openapi`）。
   - `security.permit-urls` 支持 Ant 风格路径（例如 `/public/**`）。

---

示例 curl（列出所有用户）：


示例 curl（列出所有用户）：
```bash
curl -X GET http://localhost:8088/user
```

> 注意：所有正常响应默认会被 `ApiResponse` 包装；若要跳过包装，在 Controller 或方法上使用 `@NoApiWrap`。

---

## 代码生成器（快速生成实体/Mapper/Controller） 🛠️

代码生成器：
- 入口：`src/main/java/devtools/CodeGenerator.java`，直接运行 main 方法即可
- 配置读取：`src/main/resources/application-dev.yml`（数据库连接）、`project.yml`（基础包名）
- 模板：`src/main/resources/templates/`（可自定义 entity/controller）

---


## 文档（Swagger / OpenAPI） 📚

- 启动后访问：`/swagger-ui.html` 或 `/swagger-ui/index.html`（springdoc 默认路径）  
- 原始 JSON：`/v3/api-docs`

### Swagger 极致体验亮点

- **分组、接口、响应结构全部自动生成，ApiResponse 结构和分组文档自动展示**。
- **白名单接口（如登录、注册、公开接口）无需登录即可访问，且文档中会完整显示，不会被权限拦截隐藏。**
- **所有接口分组、路径、注释均自动从数据库表名、表注释、Controller 注解生成，无需手写维护。**

---

## 开发注意事项 & 约定 ⚠️

`BaseEntity` 使用 `OffsetDateTime` 存储 `createdAt` / `updatedAt`。项目中提供了 `TimeProvider`（`src/main/java/com/github/zxs1994/java_template/util/TimeProvider.java`），其 `now()` 返回 `OffsetDateTime.now(ZoneOffset.ofHours(8))`（即固定 `+08:00`），并在 `MyMetaObjectHandler` 中用于自动填充（`createdAt` / `updatedAt`）。

- `spring.jackson.time-zone=Asia/Shanghai` 与 `spring.jackson.serialization.write-dates-as-timestamps=false`：对于 `OffsetDateTime` 来说序列化会带偏移，但该配置仍推荐保留，以保证 `LocalDateTime` / `Instant` 的序列化行为一致且对客户端友好。

## MySQL & Spring Boot 时间相关配置说明

| 配置 | 作用 | 适用类型 / 场景 | 是否对当前项目必需 |
|------|------|----------------|----------------|
| `spring.jackson.time-zone=Asia/Shanghai` | 控制 Jackson 序列化/反序列化 JSON 时使用的时区 | `java.util.Date`、`java.util.Calendar`、`Instant`；不会影响 `OffsetDateTime` 或 `LocalDateTime` | ❌ 对 `OffsetDateTime` 不必需，只影响 JSON 展示 |
| `spring.jackson.serialization.write-dates-as-timestamps=false` | 禁止将时间序列化为时间戳，改为 ISO8601 字符串格式 | 所有 Jackson 可序列化的时间类型 (`Date` / `LocalDateTime` / `OffsetDateTime`) | ✅ 推荐保留，用于保证前端可读性 |
| `SET GLOBAL time_zone = "+08:00"` | 设置 MySQL Server 默认时区 | `TIMESTAMP` 类型、带 `CURRENT_TIMESTAMP` 默认值的列 | ❌ 对 `DATETIME` 无效，不必需 |
| `-Duser.timezone=Asia/Shanghai` | 设置 JVM 默认时区 | `Date`、`Calendar`、`LocalDateTime.now()`、`OffsetDateTime.now()`（不带显式 ZoneOffset 时） | ❌ 对显式 `OffsetDateTime.now(ZoneOffset.ofHours(8))` 不必需 |
| `spring.datasource.url=jdbc:mysql://...&serverTimezone=Asia/Shanghai` | 告诉 JDBC 数据库服务端时区，用于 `TIMESTAMP` ↔ Java Date / Calendar / Instant 的自动换算 | `TIMESTAMP`、`Date`、`Instant` | ❌ 对 `DATETIME` + `OffsetDateTime` 不必需 |

---

### 小结说明

1. **OffsetDateTime + DATETIME**  
   - 当前项目使用这种组合，时间语义完全由应用层控制  
   - 数据库不做时区换算，JDBC 不干预  
   - 所以除 `spring.jackson.serialization.write-dates-as-timestamps=false` 之外，其他配置大部分是“多余的安全网/历史兼容”，可删也可留作注释

2. **Date / TIMESTAMP / CURRENT_TIMESTAMP 场景**  
   - `serverTimezone`、`SET GLOBAL time_zone`、`-Duser.timezone` 才会生效  
   - 主要目的是让 JDBC / DB 在自动转换时不漂移

3. **JSON 序列化展示**  
   - `spring.jackson.time-zone` 只影响展示，不影响 OffsetDateTime 本身  
   - OffsetDateTime 本身带 offset，Jackson 默认会按 offset 输出，无需额外时区配置


---

## 测试 & 扩展 💡

- 可添加集成测试或单元测试（当前仓库暂无测试样例）

---

## 贡献 & 联系 ❤️

欢迎提交 Issues / PR，或基于该模板进行定制化改造。

---

## 许可证
请在项目中补充 LICENSE（例如 MIT / Apache-2.0），根据你的需求选择合适的许可证。

---
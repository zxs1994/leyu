# SpringBoot + MyBatis-Plus 模板项目 📦

## 简介 💡

**这是一个基于 Spring Boot + MyBatis-Plus 的轻量模板**，提供常见 CRUD、分页、统一响应封装、枚举工具、以及代码生成器示例。适合用作新项目起步或学习参考。

---

## 主要特性 ✅

- 基于 Spring Boot 3.x（Java 17）  
- MyBatis-Plus 集成（分页、自动填充）  
- 统一 API 响应封装（`ApiResponse`），可通过 `@NoApiWrap` 取消包装  
- 内置简单的代码生成器（`devtools/CodeGenerator` + Freemarker 模板）  
- Swagger / OpenAPI 文档（springdoc）  

---

## 技术栈 🔧

- Java 17  
- Spring Boot 3.5.5  
- MyBatis-Plus  
- MySQL（示例 SQL 在 `user.sql`）  
- Freemarker（代码生成模板位于 `src/main/resources/templates`）  
- OpenAPI (springdoc) for API 文档

---

## 目录结构（关键文件）

- `src/main/java/com/example/template`：项目主代码  
  - `controller/`：`UserController`, `EnumController`  
  - `config/`：`ApiResponseWrapper`, `MyBatisPlusConfig`, `MyMetaObjectHandler`  
  - `devtools/CodeGenerator.java`：代码生成器（Main 方法）  
  - `util/`：`EnumUtils`, `LoadProperties` 等工具  
- `src/main/resources/application-dev.properties`、`application-prod.properties`：环境配置  
- `user.sql`：建表与样例数据  
- `src/main/resources/templates/`：代码生成 Freemarker 模板（entity、controller）

---

## 快速开始 🚀

### 前置条件
- JDK 17  
- Maven  
- MySQL（或修改 `application-dev.properties` 为你的数据源）

### 克隆 & 构建
```bash
git clone <repo-url>
cd springboot-mybatis-plus-template
mvn clean package
```

### 运行
- 开发（使用 dev 配置）
```bash
mvn spring-boot:run
# 或者
java -jar target/template-1.0.0.jar
```

- 生产运行示例（带 JVM 时区参数，见 `deploy.sh`）：
```bash
# 最简单启动（示例）
java -jar target/template-1.0.0.jar --spring.profiles.active=prod

# 带示例 JVM 内存配置（可选）
java -Xms512m -Xmx1g -jar target/template-1.0.0.jar --spring.profiles.active=prod
```


### 配置
- 默认激活 profile：`application.properties` 中 `spring.profiles.active=dev`  
- 开发环境数据库配置：`src/main/resources/application-dev.properties`（示例已指向 `jdbc:mysql://127.0.0.1:3306/demo?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false`）

---

## 数据库 & 样例数据 🗄️

- 建表与样例数据在 `user.sql`，包含 `user` 表结构与三条样例记录。导入后即可直接测试 API。

---

## API 示例（重要端点） 🔎

- 列表：GET /user  
- 获取：GET /user/{id}  
- 新增：POST /user  （JSON body）  
- 更新：PUT /user   （JSON body）  
- 删除：DELETE /user/{id}  
- 分页：GET /user/page?page=1&size=10  
- 枚举：GET /enums/all  （返回项目中枚举的统一列表）

示例 curl（列出所有用户）：
```bash
curl -X GET http://localhost:8088/user
```

> 注意：所有正常响应默认会被 `ApiResponse` 包装；若要跳过包装，在 Controller 或方法上使用 `@NoApiWrap`。

---

## 代码生成器（快速生成实体/Mapper/Controller） 🛠️

- 运行：在 IDE 中以 `devtools.CodeGenerator` 的 `main` 方法运行  
- 配置读取：`src/main/resources/application-dev.properties`（从中读取数据源）  
- 模板：`src/main/resources/templates`（可自定义）

---

## 文档（Swagger / OpenAPI） 📚

- 启动后访问：`/swagger-ui.html` 或 `/swagger-ui/index.html`（springdoc 默认路径）  
- 原始 JSON：`/v3/api-docs`

---

## 开发注意事项 & 约定 ⚠️

- `BaseEntity` 使用 `OffsetDateTime` 存储 `createdAt` / `updatedAt`。项目中提供了 `TimeProvider`（`src/main/java/com/example/template/util/TimeProvider.java`），其 `now()` 返回 `OffsetDateTime.now(ZoneOffset.ofHours(8))`（即固定 `+08:00`），并在 `MyMetaObjectHandler` 中用于自动填充（`createdAt` / `updatedAt`）。

- `spring.jackson.time-zone=Asia/Shanghai` 与 `spring.jackson.serialization.write-dates-as-timestamps=false`：对于 `OffsetDateTime` 来说序列化会带偏移，但该配置仍推荐保留，以保证 `LocalDateTime` / `Instant` 的序列化行为一致且对客户端友好。

## MySQL & Spring Boot 时间相关配置说明

| 配置 | 作用 | 适用类型 / 场景 | 是否对当前项目必需 |
|------|------|----------------|----------------|
| `spring.jackson.time-zone=Asia/Shanghai` | 控制 Jackson 序列化/反序列化 JSON 时使用的时区 | `java.util.Date`、`java.util.Calendar`、`Instant`；不会影响 `OffsetDateTime` 或 `LocalDateTime` | ❌ 对 `OffsetDateTime` 不必需，只影响 JSON 展示 |
| `spring.jackson.serialization.write-dates-as-timestamps=false` | 禁止将时间序列化为时间戳，改为 ISO8601 字符串格式 | 所有 Jackson 可序列化的时间类型 (`Date` / `LocalDateTime` / `OffsetDateTime`) | ✅ 推荐保留，用于保证前端可读性 |
| `SET GLOBAL time_zone = "+08:00"` | 设置 MySQL Server 默认时区 | `TIMESTAMP` 类型、带 `CURRENT_TIMESTAMP` 默认值的列 | ❌ 对 `DATETIME` 无效，不必需 |
| `-Duser.timezone=Asia/Shanghai` | 设置 JVM 默认时区 | `Date`、`Calendar`、`LocalDateTime.now()`、`OffsetDateTime.now()`（不带显式 ZoneOffset 时） | ❌ 对显式 `OffsetDateTime.now(ZoneOffset.ofHours(8))` 不必需 |
| `spring.datasource.url=jdbc:mysql://127.0.0.1:3306/demo?...&serverTimezone=Asia/Shanghai` | 告诉 JDBC 数据库服务端时区，用于 `TIMESTAMP` ↔ Java Date / Calendar / Instant 的自动换算 | `TIMESTAMP`、`Date`、`Instant` | ❌ 对 `DATETIME` + `OffsetDateTime` 不必需 |

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

- 添加集成测试或单元测试（当前仓库无测试示例）  
- 可接入 Actuator（已经添加依赖）用于监控

---

## 贡献 & 联系 ❤️

欢迎提交 Issues / PR，或基于该模板进行定制化改造。

---

## 许可证
请在项目中补充 LICENSE（例如 MIT / Apache-2.0），根据你的需求选择合适的许可证。

---
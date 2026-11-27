# 智慧城市微服务系统（Smart City Microservices）

基于 Spring Cloud 的微服务架构实践，围绕「市民提交举报 → 部门流转处理 → 数据统计分析」
这条业务链路做服务拆分，落地网关路由、JWT 无状态鉴权、统一异常处理等工程规范。

---

## 一、技术栈

| 层 | 技术选型 |
|---|---|
| 运行时 | JDK 21 |
| 基础框架 | Spring Boot 3.1.5 |
| 微服务 | Spring Cloud 2022.0.4 + Spring Cloud Alibaba 2022.0.0.0 |
| 网关 | Spring Cloud Gateway |
| 鉴权 | Spring Security + JJWT 0.11.5 |
| 持久层 | Spring Data JPA + MySQL 8 |
| 服务注册 | Nacos（默认关闭，可独立运行） |

## 二、服务拆分

```
                    ┌──────────────────────────┐
        客户端 ────→ │   gateway-service  :8081 │
                    │   统一入口 + 路由分发      │
                    └────────────┬─────────────┘
                                 │
        ┌──────────┬─────────────┼─────────────┬──────────┐
        │          │             │             │          │
   ┌────▼────┐┌────▼─────┐┌──────▼──────┐┌─────▼────┐
   │  user   ││  report  ││     ai      ││   data   │
   │ :8000   ││  :8002   ││    :8003    ││  :8004   │
   │ 用户与  ││ 举报工单 ││  AI 辅助    ││ 数据采集 │
   │ 鉴权    ││ 流转处理 ││  分析       ││ 与统计   │
   └─────────┘└──────────┘└─────────────┘└──────────┘
        │          │
        └──────────┴──→ MySQL (smart_city)
```

### 各服务职责

| 服务 | 端口 | 职责 | 对外前缀 |
|---|---|---|---|
| gateway-service | 8081 | 统一入口、路由转发、跨域处理 | — |
| user-service | 8000 | 用户注册登录、JWT 签发与校验、角色权限 | `/api/users/**` |
| report-service | 8002 | 举报工单 CRUD、部门流转、状态机 | `/api/reports/**` |
| ai-service | 8003 | 举报内容智能分类与辅助分析 | `/api/ai/**` |
| data-service | 8004 | 行情类数据采集、统计聚合 | `/api/data/**` |

**拆分依据**：按业务能力（用户域 / 工单域 / 智能域 / 数据域）切，而不是按技术层切。
每个服务拥有自己的数据表和独立启动能力，网关是唯一对外暴露点。

## 三、鉴权设计

采用 **JWT 无状态鉴权**，不依赖服务端 Session：

```
① 登录  →  user-service 校验用户名密码
        →  JwtUtil.generateToken() 签发 token
        →  claims 内含 userId、role

② 请求  →  客户端携带 Authorization: Bearer <token>
        →  JwtAuthenticationFilter 拦截
        →  JwtUtil 解析 claims 并塞入 SecurityContext
        →  Spring Security 据此判定接口权限

③ 校验  →  validateToken() 检查签名与过期时间
        →  refreshToken() 支持续签
```

**为什么用 JWT 而不是 Session**：微服务多实例部署时，Session 需要额外做共享存储（Redis）
或粘性会话；JWT 把身份信息放在 token 自身，任何实例都能独立校验，横向扩展无成本。
代价是 token 无法主动失效（需要额外维护黑名单），本项目作为演示未引入。

> ⚠️ JWT 签名密钥必须通过环境变量 `JWT_SECRET` 注入，长度至少 32 字节。
> 代码中未提供任何硬编码默认值，未配置时会抛异常而不是静默使用弱密钥。

## 四、目录结构

```
smart-city-microservices/
├── pom.xml                          # 父 POM，统一版本管理
├── gateway-service/
│   └── src/main/
│       ├── java/com/zhao/gateway/
│       │   ├── GatewayServiceApplication.java
│       │   └── controller/GatewayController.java
│       └── resources/application.yml
├── user-service/
│   └── src/main/
│       ├── java/com/zhao/user/
│       │   ├── UserServiceApplication.java
│       │   ├── config/           # SecurityConfig / CorsConfig
│       │   ├── controller/       # UserController
│       │   ├── dao/              # UserRepository
│       │   ├── entity/           # User
│       │   ├── filter/           # JwtAuthenticationFilter
│       │   ├── service/          # UserDetailsService 实现
│       │   └── util/             # JwtUtil / AuthUtil
│       └── resources/application.yml
├── report-service/
│   └── src/main/java/com/zhao/report/
│       ├── config/               # CorsConfig
│       ├── controller/           # ReportController
│       ├── dao/                  # CitizenReportRepository
│       ├── entity/               # CitizenReport
│       └── exception/            # GlobalExceptionHandler
├── ai-service/
│   └── src/main/java/com/zhao/ai/
│       ├── controller/           # AIController
│       ├── service/              # AIAnalyzer
│       └── util/                 # DepartmentConstants
└── data-service/
    └── src/main/java/com/zhao/data/
        ├── controller/           # DataController
        ├── entity/               # AirQualityData
        └── service/              # DataFetcher
```

## 五、快速开始

### 1. 准备数据库

```sql
CREATE DATABASE IF NOT EXISTS smart_city DEFAULT CHARSET utf8mb4;
```

JPA 配置为 `ddl-auto: update`，首次启动会自动建表。

### 2. 配置环境变量

所有敏感项已外置，不提交到仓库：

```bash
# 数据库
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=smart_city
export DB_USER=your_user
export DB_PASSWORD=your_password

# JWT（至少 32 字节）
export JWT_SECRET=your-very-long-random-secret-at-least-32-bytes

# 管理员账号
export ADMIN_USER=admin
export ADMIN_PASSWORD=your_password
```

### 3. 启动顺序

```bash
mvn clean install -DskipTests

# 按依赖顺序启动（可分别在不同终端）
java -jar user-service/target/user-service-1.0.0.jar
java -jar report-service/target/report-service-1.0.0.jar
java -jar ai-service/target/ai-service-1.0.0.jar
java -jar data-service/target/data-service-1.0.0.jar
java -jar gateway-service/target/gateway-service-1.0.0.jar
```

网关配置支持通过环境变量覆盖各服务地址：

```bash
export USER_SERVICE_URI=http://localhost:8000
export REPORT_SERVICE_URI=http://localhost:8002
export AI_SERVICE_URI=http://localhost:8003
export DATA_SERVICE_URI=http://localhost:8004
```

### 4. 验证

```bash
curl http://localhost:8081/api/users/...
```

## 六、工程规范实践

**1. 统一异常处理**
`report-service/exception/GlobalExceptionHandler.java` 用 `@RestControllerAdvice`
集中处理业务异常与参数校验异常，避免每个 Controller 重复写 try-catch，
也保证返回结构一致（不把堆栈泄露给前端）。

**2. 统一跨域策略**
每个服务都有 `CorsConfig`，而非依赖前端代理。好处是服务被直接调用时也能正常工作，
不因为绕过网关而失效。

**3. 统一响应结构**
返回体统一为 `{code, message, data}` 形式，前端只需处理一套解包逻辑。

**4. 版本集中管理**
父 POM 用 `dependencyManagement` 统一 Spring Boot / Spring Cloud / Spring Cloud Alibaba
的版本，子模块只声明 `artifactId`，避免版本漂移。

## 七、已知局限

- **服务间调用是硬编码 URI 而非服务发现**：Nacos 已在依赖中，但配置为 `enabled: false`。
  生产环境应开启 Nacos 并用服务名路由。
- **没有链路追踪**：多服务排障困难，应补 Micrometer Tracing + Zipkin。
- **没有熔断降级**：gateway 与下游之间缺少 Sentinel 或 Resilience4j 保护。
- **没有分布式事务**：跨服务写操作目前无补偿机制，真实业务场景需要引入 Saga 或消息最终一致。
- **ai-service 的 AI 分析为规则匹配**，不是模型推理。

这些是架构演进的下一步，不是当前实现的缺陷。

## 八、License

MIT

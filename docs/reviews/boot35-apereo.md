# Shiro CAS Boot 3.5 迁移评审

关联 [Issue #4](https://github.com/easy-4-java/shiro-cas-spring-boot-starter/issues/4)。[候选补丁](boot35-apereo-4.0.4.patch)以功能分支提交 `42ed09d32de0f2c91ec1a5b8c0232d4b8a3d772d` 为基线，供评审后应用。补丁采用 Apereo CAS Client 4.0.4，保留 Spring Boot 3.5.16 和 Java 17。实际检查发布 JAR 字节码，4.0.4 为 Java 17，4.1.0 和 4.1.1 为 Java 21。 [Apereo 4.0.4 发布记录](https://github.com/apereo/java-cas-client/releases/tag/cas-client-4.0.4)

## 启动与协议验证

- Jasig 3.6.4 的 Servlet 类型使用 `javax.servlet`。候选补丁使用 Apereo 的 Jakarta 过滤器与会话监听器，补齐 Boot 3 自动配置入口、排序元数据及 Realm 和过滤器的注册顺序。已有 Shiro Biz 3.5 根据 `subjectFactory` 名称注入 CAS 工厂。
- 只补自动配置入口时会出现同名 Bean 冲突。补齐排序后，保留旧继承式配置仍可复现 Realm 与安全管理器的循环依赖。候选把 Realm 创建移出过滤器配置，隔离启动通过。
- 沿用 Boot 2.7 的回归，覆盖显式 service URL 优先级、缺失匹配模式和布尔 remember-me 属性。复用 Boot 4.1 的 Jakarta 注册与回环票据交换测试。

| 验收 | 结果 |
| --- | --- |
| Java 17.0.21 与 Maven 3.9.16，`clean verify` | 退出 0 |
| Surefire | 42 项，零失败、零错误、零跳过 |
| JaCoCo 行覆盖率 | 729/804，90.67%，通过原有 90% 门禁 |
| CAS Web 注册 | CAS1、CAS2、CAS3、SAML11 过滤器、监听器及登出重定向通过 |
| 回环服务验证 | CAS2/CAS3 的票据交换、拒绝和断言授权通过 |

本地构建从固定源码构建 Easy4J 快照。最终 Shiro Biz 来源为 [`32e3746`](https://github.com/easy-4-java/shiro-extension-spring-boot-starter/commit/32e37463bbe9afcaf1df42923531b59a8cc0ce21)，其合并来源对应 [`d346ac8`](https://github.com/easy-4-java/shiro-extension-spring-boot-starter/commit/d346ac8b06aed6321de37ba0e01a0c6ccd9d97fd)。其依赖源码为 JWT Issuer `67490d6`、Shiro J2Cache `0d8df10` 和 Shiro Extension `3f837a9`。在独立检出应用补丁后，以 JDK 17 和含这些本地快照的 Maven 仓库运行 `mvn -B -ntp -nsu clean verify`。真实 CAS 部署联调和远端 CI 待候选获批后推进。

## 依赖与兼容性

CAS 4.0.4 默认解析 Nimbus JOSE JWT 9.37.3。候选锁定修复版 9.37.4，OSV 对 84 个实际解析的运行依赖报告 3 个不同的公告 ID，涉及 Shiro 2.2.1 和 Spring MVC 6.2.19。 [Nimbus 公告](https://github.com/advisories/GHSA-xwmg-2g98-w7v9)

`ShiroCasProperties` 的协议类型与 `CasAssertionAuthenticationToken` 的断言类型从 `org.jasig.cas.client` 迁移到 `org.apereo.cas.client`。CAS 自动配置类的父类与继承成员也会改变。维护分支采用该 API 之前需确认迁移范围。Issue #4 仍开放。

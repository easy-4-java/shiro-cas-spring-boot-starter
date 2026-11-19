# Issue #4 的 Boot 2.7 自动配置回归

Spring Boot 通过 `@EnableAutoConfiguration` 加载 Shiro CAS 时，原空元数据把配置类标记为已处理，导致 Spring 忽略源码上的排序注解。补齐普通启动回归后，五个配置场景均因 `subjectFactory` 重复注册失败。

移除不完整的自动配置元数据后，Spring 使用现有配置排序注解，CAS Realm 及过滤器注册恢复正常。JDK 8 的 `clean verify` 执行 37 项，零失败、零错误、零跳过，覆盖 CAS1、CAS2、CAS3、SAML11 的过滤器注册以及 CAS2 配置绑定。Actuator 仅作为测试依赖，用于验证普通 Spring Boot 自动配置入口。尚余安全依赖与公共类型兼容性继续在 Issue #4 跟踪。[Issue #4](https://github.com/easy-4-java/shiro-cas-spring-boot-starter/issues/4)

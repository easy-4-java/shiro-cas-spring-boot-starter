package org.apache.shiro.spring.boot;

import org.apache.shiro.mgt.SubjectFactory;
import org.apache.shiro.spring.boot.cas.CasSubjectFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Primary;

import java.util.stream.Collectors;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.biz.authz.principal.ShiroPrincipalRepository;
import org.apache.shiro.biz.realm.AuthorizingRealmListener;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.boot.cas.realm.CasAssertionAuthorizingRealm;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration for Shiro CAS web authentication.
 * <p>Registers CAS-specific subject factory and configuration when CAS is enabled.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@AutoConfiguration(beforeName = {
	"org.apache.shiro.spring.config.web.autoconfigure.ShiroWebAutoConfiguration",  // shiro-spring-boot-web-starter
	"org.apache.shiro.spring.boot.ShiroBizWebAutoConfiguration" // shiro-biz-spring-boot-starter
})
/**
 * <p>Configuration properties.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@ConditionalOnProperty(prefix = ShiroCasProperties.PREFIX, value = "enabled", havingValue = "true")
@EnableConfigurationProperties({ ShiroBizProperties.class, ShiroCasProperties.class })
public class ShiroCasWebAutoConfiguration {

	/**
	 * Creates the CAS subject factory.
	 * @return the CAS subject factory
	 */
	@Bean
	@Primary
    protected SubjectFactory casSubjectFactory() {
        return new CasSubjectFactory();
    }
	
	/**
	 * cas Realm.
	 *
	 * @param repositoryProvider the repository provider
	 * @param realmsListenerProvider the realms listener provider
	 * @return the result
	 */
	@Bean
	public Realm casRealm(ShiroCasProperties casProperties, ShiroBizProperties bizProperties,
						  ObjectProvider<ShiroPrincipalRepository> repositoryProvider,
						  ObjectProvider<AuthorizingRealmListener> realmsListenerProvider) {

		CasAssertionAuthorizingRealm casRealm = new CasAssertionAuthorizingRealm(casProperties);
		//认证账号信息提供实现：认证信息、角色信息、权限信息；业务系统需要自己实现该接口
		casRealm.setRepository(repositoryProvider.getIfAvailable());
		//凭证匹配器：该对象主要做密码校验
		casRealm.setCredentialsMatcher(new AllowAllCredentialsMatcher());
		//Realm 执行监听：实现该接口可监听认证失败和成功的状态，从而做业务系统自己的事情，比如记录日志
		casRealm.setRealmsListeners(realmsListenerProvider.stream().collect(Collectors.toList()));
		//缓存相关的配置：采用提供的默认配置即可
		casRealm.setCachingEnabled(bizProperties.isCachingEnabled());
		//认证缓存配置
		casRealm.setAuthenticationCachingEnabled(bizProperties.isAuthenticationCachingEnabled());
		casRealm.setAuthenticationCacheName(bizProperties.getAuthenticationCacheName());
		//授权缓存配置
		casRealm.setAuthorizationCachingEnabled(bizProperties.isAuthorizationCachingEnabled());
		casRealm.setAuthorizationCacheName(bizProperties.getAuthorizationCacheName());

		return casRealm;
	}

}

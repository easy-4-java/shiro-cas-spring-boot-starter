package org.apache.shiro.spring.boot.cas;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apereo.cas.client.Protocol;

/**
 * <p>Configuration properties for Cas Client.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class CasClientProperties{

	// default name of the CAS attribute for remember me authentication (CAS 3.4.10+)
    public static final String DEFAULT_REMEMBER_ME_ATTRIBUTE_NAME = "longTermAuthenticationRequestTokenUsed";
	
	/** DEFAULT,JNDI,WEB_XML,PROPERTY_FILE,SYSTEM_PROPERTIES */
	private String configurationStrategy;
	/** Defines the location of the CAS server login URL, i.e. https://localhost:8443/cas/login */
	private String casServerLoginUrl;
	/** Defines the location of the CAS server logout URL, i.e. https://localhost:8443/cas/logout */
	private String casServerLogoutUrl;
	/** Defines the location of the CAS server rest URL, i.e. https://localhost:8443/cas/v1/tickets */
	private String casServerRestUrl;
	/** The prefix url of the CAS server. i.e.https://localhost:8443/cas */
	private String casServerUrlPrefix;
    /** Defaults to true */
	private boolean eagerlyCreateSessions = true;
    /** Specifies whether any proxy is OK. Defaults to false. */
	private boolean acceptAnyProxy = false;
	/**
	 * Specifies the proxy chain. 
	 * Each acceptable proxy chain should include a space-separated list of URLs (for exact match) or regular expressions of URLs (starting by the ^ character). 
	 * Each acceptable proxy chain should appear on its own line.
	 */
	private String allowedProxyChains;
	/** Specifies the name of the request parameter on where to find the artifact (i.e. ticket). */
	private String artifactParameterName = "ticket";
	private boolean artifactParameterOverPost = false;
	/** The Url Patterns of AssertionThreadLocalFilter. */
	private String[] assertionThreadLocalFilterUrlPatterns = new String[] { "/*" };
	/** The class name of the component to decide how to handle authn redirects to CAS */
	private String authenticationRedirectStrategyClass;
	/** The Url Patterns of AuthenticationFilter. */
	private String[] authenticationFilterUrlPatterns = new String[] { "/*" };
	/** The algorithm used by the proxyGrantingTicketStorageClass if it supports encryption. Defaults to DESede */
	private String cipherAlgorithm;
	/** Default url to redirect to, in case no erorr matches are found. */
	private String defaultErrorRedirectPage;
	/** Whether Enable Cas. */
	private boolean enabled = false;
	/** Specifies the encoding charset the client should use */
	private String encoding = "UTF-8";
	/** Whether Enable ErrorRedirectFilter. */
	private boolean errorRedirect = false;
	/** The Url to redirect to, find the path by Fully qualified exception name , i.e. java.lang.Exception . */
	private Map<String /* Class Name */, String /* Redirect Page Path */> errorRedirectMappings = new LinkedHashMap<String, String>();
	/** The Url Patterns of ErrorRedirectFilter. */
	private String[] errorRedirectFilterUrlPatterns = new String[] { "/*" };
	/** Whether the client should auto encode the service url. Defaults to true */
	private boolean encodeServiceUrl = true;
	/** Whether to throw an exception or not on ticket validation failure. Defaults to true. */
	private boolean exceptionOnValidationFailure = true;
	/** the url where the application is redirected if the CAS service ticket validation failed (example : /mycontextpatch/cas_error.jsp) */
	private String failureUrl;
	/** Specifies whether gateway=true should be sent to the CAS server. Valid values are either true/false (or no value at all) */
	private boolean gateway = false;
	/** The storage class used to record gateway requests */
	private String gatewayStorageClass;
	/** Hostname verifier class name, used when making back-channel calls */
	private String hostnameVerifier;
	private String hostnameVerifierConfig;
	/** Whether role checking should ignore case. Defaults to false. */
	private boolean ignoreCase = false;
	/** Defines the url pattern to ignore, when intercepting authentication requests. */
	private String ignorePattern;
	/** Defines the type of the pattern specified. Defaults to REGEX. Other types are CONTAINS, EXACT. */
	private String ignoreUrlPatternType = "REGEX";

	private boolean ignoreInitConfiguration = false;
	/** Defaults to logoutRequest */
	private String logoutParameterName;
	/** Startup delay for the cleanup task to remove expired tickets from the storage. Defaults to 60000 msec */
	private long millisBetweenCleanUps = 60000L;
	/** The protocol of the CAS Client. */
	private Protocol protocol = Protocol.CAS2;
	/** The callback URL to provide the CAS server to accept Proxy Granting Tickets. */
	private String proxyCallbackUrl;
	/**
	 * The URL to watch for PGTIOU/PGT responses from the CAS server. 
	 * Should be defined from the root of the context. 
	 * For example, if your application is deployed in /cas-client-app and 
	 * you want the proxy receptor URL to be /cas-client-app/my/receptor 
	 * you need to configure proxyReceptorUrl to be /my/receptor.
	 */
	private String proxyReceptorUrl;
	/** Specify an implementation of the ProxyGrantingTicketStorage class that has a no-arg constructor. */
	private String proxyGrantingTicketStorageClass;
	/** The Url Patterns of HttpServletRequestWrapperFilter. */
	private String[] requestWrapperFilterUrlPatterns = new String[] { "/*" };
	/** Whether to redirect to the same URL after ticket validation, but without the ticket in the parameter. Defaults to true. */
	private boolean redirectAfterValidation = true;
	/**
	 * Specifies whether renew=true should be sent to the CAS server. 
	 * Valid values are either true/false (or no value at all). 
	 * Note that renew cannot be specified as local init-param setting..
	 */
	private boolean renew = false;
	/** Name of parameter containing the state of the CAS server webflow. */
	private String relayStateParameterName;
	/** Used to determine the principal role. */
	private String roleAttribute;
	/** default name of the CAS attribute for remember me authentication (CAS 3.4.10+) */
    private String rememberMeAttributeName = DEFAULT_REMEMBER_ME_ATTRIBUTE_NAME;
	/** The secret key used by the proxyGrantingTicketStorageClass if it supports encryption. */
	private String secretKey;
	/** Defines the location of the application cas callback URL, i.e. /callback */
	private String serverCallbackUrl;
	/**
	 * The name of the server this application is hosted on. 
	 * Service URL will be dynamically constructed using this, 
	 * i.e. https://localhost:8443 (you must include the protocol, but port is optional if it's a standard port).
	 */
	private String serverName;
	/** The service URL to send to the CAS server, i.e. https://localhost:8443/yourwebapp/index.html */
	private String service;
	/** Specifies the name of the request parameter on where to find the service (i.e. service). */
	private String serviceParameterName = "service";
	/** The Url Patterns of SingleSignOutFilter. */
	private String[] signOutFilterUrlPatterns = new String[] { "/*" };
	/**
	 * A reference to a properties file that includes SSL settings for client-side
	 * SSL config, used during back-channel calls. The configuration includes keys
	 * for protocol which defaults to SSL,keyStoreType, keyStorePath,
	 * keyStorePass,keyManagerType which defaults to SunX509 andcertificatePassword.
	 */
	private String sslConfigFile;
	/** The Url Patterns of TicketValidationFilter. */
	private String[] ticketValidationFilterUrlPatterns = new String[] { "/*" };
	/** Ticket validator class to use/create */
	private String ticketValidatorClass;
	/**
	 * The tolerance for drifting clocks when validating SAML tickets. 
	 * Note that 10 seconds should be more than enough for most environments that have NTP time synchronization. 
	 * Defaults to 1000 msec
	 */
	private long tolerance = 5000L;
	/**
	 * Whether to store the Assertion in session or not. If sessions are not used,
	 * tickets will be required for each request. Defaults to true.
	 */
	private boolean useSession = true;

	/**
	 * Returns the configuration strategy.
	 *
	 * @return the configuration strategy
	 */
	public String getConfigurationStrategy() {
		return configurationStrategy;
	}

	/**
	 * Sets the configuration strategy.
	 *
	 * @param configurationStrategy the configuration strategy
	 */
	public void setConfigurationStrategy(String configurationStrategy) {
		this.configurationStrategy = configurationStrategy;
	}

	/**
	 * Returns the cas server login url.
	 *
	 * @return the cas server login url
	 */
	public String getCasServerLoginUrl() {
		return casServerLoginUrl;
	}

	/**
	 * Sets the cas server login url.
	 *
	 * @param casServerLoginUrl the cas server login url
	 */
	public void setCasServerLoginUrl(String casServerLoginUrl) {
		this.casServerLoginUrl = casServerLoginUrl;
	}
	
	/**
	 * Returns the cas server logout url.
	 *
	 * @return the cas server logout url
	 */
	public String getCasServerLogoutUrl() {
		return casServerLogoutUrl;
	}

	/**
	 * Sets the cas server logout url.
	 *
	 * @param casServerLogoutUrl the cas server logout url
	 */
	public void setCasServerLogoutUrl(String casServerLogoutUrl) {
		this.casServerLogoutUrl = casServerLogoutUrl;
	}

	/**
	 * Returns the cas server rest url.
	 *
	 * @return the cas server rest url
	 */
	public String getCasServerRestUrl() {
		return casServerRestUrl;
	}

	/**
	 * Sets the cas server rest url.
	 *
	 * @param casServerRestUrl the cas server rest url
	 */
	public void setCasServerRestUrl(String casServerRestUrl) {
		this.casServerRestUrl = casServerRestUrl;
	}

	/**
	 * Returns the cas server url prefix.
	 *
	 * @return the cas server url prefix
	 */
	public String getCasServerUrlPrefix() {
		return casServerUrlPrefix;
	}

	/**
	 * Sets the cas server url prefix.
	 *
	 * @param casServerUrlPrefix the cas server url prefix
	 */
	public void setCasServerUrlPrefix(String casServerUrlPrefix) {
		this.casServerUrlPrefix = casServerUrlPrefix;
	}

	/**
	 * Returns the eagerly create sessions.
	 *
	 * @return the eagerly create sessions
	 */
	public boolean isEagerlyCreateSessions() {
		return eagerlyCreateSessions;
	}

	/**
	 * Sets the eagerly create sessions.
	 *
	 * @param eagerlyCreateSessions the eagerly create sessions
	 */
	public void setEagerlyCreateSessions(boolean eagerlyCreateSessions) {
		this.eagerlyCreateSessions = eagerlyCreateSessions;
	}

	/**
	 * Returns the accept any proxy.
	 *
	 * @return the accept any proxy
	 */
	public boolean isAcceptAnyProxy() {
		return acceptAnyProxy;
	}

	/**
	 * Sets the accept any proxy.
	 *
	 * @param acceptAnyProxy the accept any proxy
	 */
	public void setAcceptAnyProxy(boolean acceptAnyProxy) {
		this.acceptAnyProxy = acceptAnyProxy;
	}

	/**
	 * Returns the allowed proxy chains.
	 *
	 * @return the allowed proxy chains
	 */
	public String getAllowedProxyChains() {
		return allowedProxyChains;
	}

	/**
	 * Sets the allowed proxy chains.
	 *
	 * @param allowedProxyChains the allowed proxy chains
	 */
	public void setAllowedProxyChains(String allowedProxyChains) {
		this.allowedProxyChains = allowedProxyChains;
	}

	/**
	 * Returns the artifact parameter name.
	 *
	 * @return the artifact parameter name
	 */
	public String getArtifactParameterName() {
		return artifactParameterName;
	}

	/**
	 * Sets the artifact parameter name.
	 *
	 * @param artifactParameterName the artifact parameter name
	 */
	public void setArtifactParameterName(String artifactParameterName) {
		this.artifactParameterName = artifactParameterName;
	}

	/**
	 * Returns the artifact parameter over post.
	 *
	 * @return the artifact parameter over post
	 */
	public boolean isArtifactParameterOverPost() {
		return artifactParameterOverPost;
	}

	/**
	 * Sets the artifact parameter over post.
	 *
	 * @param artifactParameterOverPost the artifact parameter over post
	 */
	public void setArtifactParameterOverPost(boolean artifactParameterOverPost) {
		this.artifactParameterOverPost = artifactParameterOverPost;
	}

	/**
	 * Returns the assertion thread local filter url patterns.
	 *
	 * @return the assertion thread local filter url patterns
	 */
	public String[] getAssertionThreadLocalFilterUrlPatterns() {
		return assertionThreadLocalFilterUrlPatterns;
	}

	/**
	 * Sets the assertion thread local filter url patterns.
	 *
	 * @param assertionThreadLocalFilterUrlPatterns the assertion thread local filter url patterns
	 */
	public void setAssertionThreadLocalFilterUrlPatterns(String[] assertionThreadLocalFilterUrlPatterns) {
		this.assertionThreadLocalFilterUrlPatterns = assertionThreadLocalFilterUrlPatterns;
	}

	/**
	 * Returns the authentication redirect strategy class.
	 *
	 * @return the authentication redirect strategy class
	 */
	public String getAuthenticationRedirectStrategyClass() {
		return authenticationRedirectStrategyClass;
	}

	/**
	 * Sets the authentication redirect strategy class.
	 *
	 * @param authenticationRedirectStrategyClass the authentication redirect strategy class
	 */
	public void setAuthenticationRedirectStrategyClass(String authenticationRedirectStrategyClass) {
		this.authenticationRedirectStrategyClass = authenticationRedirectStrategyClass;
	}

	/**
	 * Returns the authentication filter url patterns.
	 *
	 * @return the authentication filter url patterns
	 */
	public String[] getAuthenticationFilterUrlPatterns() {
		return authenticationFilterUrlPatterns;
	}

	/**
	 * Sets the authentication filter url patterns.
	 *
	 * @param authenticationFilterUrlPatterns the authentication filter url patterns
	 */
	public void setAuthenticationFilterUrlPatterns(String[] authenticationFilterUrlPatterns) {
		this.authenticationFilterUrlPatterns = authenticationFilterUrlPatterns;
	}

	/**
	 * Returns the cipher algorithm.
	 *
	 * @return the cipher algorithm
	 */
	public String getCipherAlgorithm() {
		return cipherAlgorithm;
	}

	/**
	 * Sets the cipher algorithm.
	 *
	 * @param cipherAlgorithm the cipher algorithm
	 */
	public void setCipherAlgorithm(String cipherAlgorithm) {
		this.cipherAlgorithm = cipherAlgorithm;
	}

	/**
	 * Returns the default error redirect page.
	 *
	 * @return the default error redirect page
	 */
	public String getDefaultErrorRedirectPage() {
		return defaultErrorRedirectPage;
	}

	/**
	 * Sets the default error redirect page.
	 *
	 * @param defaultErrorRedirectPage the default error redirect page
	 */
	public void setDefaultErrorRedirectPage(String defaultErrorRedirectPage) {
		this.defaultErrorRedirectPage = defaultErrorRedirectPage;
	}

	/**
	 * Returns the enabled.
	 *
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Sets the enabled.
	 *
	 * @param enabled the enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Returns the encoding.
	 *
	 * @return the encoding
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * Sets the encoding.
	 *
	 * @param encoding the encoding
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * Returns the error redirect.
	 *
	 * @return the error redirect
	 */
	public boolean isErrorRedirect() {
		return errorRedirect;
	}

	/**
	 * Sets the error redirect.
	 *
	 * @param errorRedirect the error redirect
	 */
	public void setErrorRedirect(boolean errorRedirect) {
		this.errorRedirect = errorRedirect;
	}

	/**
	 * Returns the error redirect mappings.
	 *
	 * @return the error redirect mappings
	 */
	public Map<String, String> getErrorRedirectMappings() {
		return errorRedirectMappings;
	}

	/**
	 * Sets the error redirect mappings.
	 *
	 * @param errorRedirectMappings the error redirect mappings
	 */
	public void setErrorRedirectMappings(Map<String, String> errorRedirectMappings) {
		this.errorRedirectMappings = errorRedirectMappings;
	}

	/**
	 * Returns the error redirect filter url patterns.
	 *
	 * @return the error redirect filter url patterns
	 */
	public String[] getErrorRedirectFilterUrlPatterns() {
		return errorRedirectFilterUrlPatterns;
	}

	/**
	 * Sets the error redirect filter url patterns.
	 *
	 * @param errorRedirectFilterUrlPatterns the error redirect filter url patterns
	 */
	public void setErrorRedirectFilterUrlPatterns(String[] errorRedirectFilterUrlPatterns) {
		this.errorRedirectFilterUrlPatterns = errorRedirectFilterUrlPatterns;
	}

	/**
	 * Returns the encode service url.
	 *
	 * @return the encode service url
	 */
	public boolean isEncodeServiceUrl() {
		return encodeServiceUrl;
	}

	/**
	 * Sets the encode service url.
	 *
	 * @param encodeServiceUrl the encode service url
	 */
	public void setEncodeServiceUrl(boolean encodeServiceUrl) {
		this.encodeServiceUrl = encodeServiceUrl;
	}

	/**
	 * Returns the exception on validation failure.
	 *
	 * @return the exception on validation failure
	 */
	public boolean isExceptionOnValidationFailure() {
		return exceptionOnValidationFailure;
	}

	/**
	 * Sets the exception on validation failure.
	 *
	 * @param exceptionOnValidationFailure the exception on validation failure
	 */
	public void setExceptionOnValidationFailure(boolean exceptionOnValidationFailure) {
		this.exceptionOnValidationFailure = exceptionOnValidationFailure;
	}

	/**
	 * Returns the failure url.
	 *
	 * @return the failure url
	 */
	public String getFailureUrl() {
		return failureUrl;
	}

	/**
	 * Sets the failure url.
	 *
	 * @param failureUrl the failure url
	 */
	public void setFailureUrl(String failureUrl) {
		this.failureUrl = failureUrl;
	}

	/**
	 * Returns the gateway.
	 *
	 * @return the gateway
	 */
	public boolean isGateway() {
		return gateway;
	}

	/**
	 * Sets the gateway.
	 *
	 * @param gateway the gateway
	 */
	public void setGateway(boolean gateway) {
		this.gateway = gateway;
	}

	/**
	 * Returns the gateway storage class.
	 *
	 * @return the gateway storage class
	 */
	public String getGatewayStorageClass() {
		return gatewayStorageClass;
	}

	/**
	 * Sets the gateway storage class.
	 *
	 * @param gatewayStorageClass the gateway storage class
	 */
	public void setGatewayStorageClass(String gatewayStorageClass) {
		this.gatewayStorageClass = gatewayStorageClass;
	}

	/**
	 * Returns the hostname verifier.
	 *
	 * @return the hostname verifier
	 */
	public String getHostnameVerifier() {
		return hostnameVerifier;
	}

	/**
	 * Sets the hostname verifier.
	 *
	 * @param hostnameVerifier the hostname verifier
	 */
	public void setHostnameVerifier(String hostnameVerifier) {
		this.hostnameVerifier = hostnameVerifier;
	}

	/**
	 * Returns the hostname verifier config.
	 *
	 * @return the hostname verifier config
	 */
	public String getHostnameVerifierConfig() {
		return hostnameVerifierConfig;
	}

	/**
	 * Sets the hostname verifier config.
	 *
	 * @param hostnameVerifierConfig the hostname verifier config
	 */
	public void setHostnameVerifierConfig(String hostnameVerifierConfig) {
		this.hostnameVerifierConfig = hostnameVerifierConfig;
	}

	/**
	 * Returns the ignore case.
	 *
	 * @return the ignore case
	 */
	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	/**
	 * Sets the ignore case.
	 *
	 * @param ignoreCase the ignore case
	 */
	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	/**
	 * Returns the ignore pattern.
	 *
	 * @return the ignore pattern
	 */
	public String getIgnorePattern() {
		return ignorePattern;
	}

	/**
	 * Sets the ignore pattern.
	 *
	 * @param ignorePattern the ignore pattern
	 */
	public void setIgnorePattern(String ignorePattern) {
		this.ignorePattern = ignorePattern;
	}

	/**
	 * Returns the ignore url pattern type.
	 *
	 * @return the ignore url pattern type
	 */
	public String getIgnoreUrlPatternType() {
		return ignoreUrlPatternType;
	}

	/**
	 * Sets the ignore url pattern type.
	 *
	 * @param ignoreUrlPatternType the ignore url pattern type
	 */
	public void setIgnoreUrlPatternType(String ignoreUrlPatternType) {
		this.ignoreUrlPatternType = ignoreUrlPatternType;
	}

	/**
	 * Returns the ignore init configuration.
	 *
	 * @return the ignore init configuration
	 */
	public boolean isIgnoreInitConfiguration() {
		return ignoreInitConfiguration;
	}

	/**
	 * Sets the ignore init configuration.
	 *
	 * @param ignoreInitConfiguration the ignore init configuration
	 */
	public void setIgnoreInitConfiguration(boolean ignoreInitConfiguration) {
		this.ignoreInitConfiguration = ignoreInitConfiguration;
	}

	/**
	 * Returns the logout parameter name.
	 *
	 * @return the logout parameter name
	 */
	public String getLogoutParameterName() {
		return logoutParameterName;
	}

	/**
	 * Sets the logout parameter name.
	 *
	 * @param logoutParameterName the logout parameter name
	 */
	public void setLogoutParameterName(String logoutParameterName) {
		this.logoutParameterName = logoutParameterName;
	}

	/**
	 * Returns the millis between clean ups.
	 *
	 * @return the millis between clean ups
	 */
	public long getMillisBetweenCleanUps() {
		return millisBetweenCleanUps;
	}

	/**
	 * Sets the millis between clean ups.
	 *
	 * @param millisBetweenCleanUps the millis between clean ups
	 */
	public void setMillisBetweenCleanUps(long millisBetweenCleanUps) {
		this.millisBetweenCleanUps = millisBetweenCleanUps;
	}

	/**
	 * Returns the protocol.
	 *
	 * @return the protocol
	 */
	public Protocol getProtocol() {
		return protocol;
	}

	/**
	 * Sets the protocol.
	 *
	 * @param protocol the protocol
	 */
	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	/**
	 * Returns the proxy callback url.
	 *
	 * @return the proxy callback url
	 */
	public String getProxyCallbackUrl() {
		return proxyCallbackUrl;
	}

	/**
	 * Sets the proxy callback url.
	 *
	 * @param proxyCallbackUrl the proxy callback url
	 */
	public void setProxyCallbackUrl(String proxyCallbackUrl) {
		this.proxyCallbackUrl = proxyCallbackUrl;
	}

	/**
	 * Returns the proxy receptor url.
	 *
	 * @return the proxy receptor url
	 */
	public String getProxyReceptorUrl() {
		return proxyReceptorUrl;
	}

	/**
	 * Sets the proxy receptor url.
	 *
	 * @param proxyReceptorUrl the proxy receptor url
	 */
	public void setProxyReceptorUrl(String proxyReceptorUrl) {
		this.proxyReceptorUrl = proxyReceptorUrl;
	}

	/**
	 * Returns the proxy granting ticket storage class.
	 *
	 * @return the proxy granting ticket storage class
	 */
	public String getProxyGrantingTicketStorageClass() {
		return proxyGrantingTicketStorageClass;
	}

	/**
	 * Sets the proxy granting ticket storage class.
	 *
	 * @param proxyGrantingTicketStorageClass the proxy granting ticket storage class
	 */
	public void setProxyGrantingTicketStorageClass(String proxyGrantingTicketStorageClass) {
		this.proxyGrantingTicketStorageClass = proxyGrantingTicketStorageClass;
	}

	/**
	 * Returns the request wrapper filter url patterns.
	 *
	 * @return the request wrapper filter url patterns
	 */
	public String[] getRequestWrapperFilterUrlPatterns() {
		return requestWrapperFilterUrlPatterns;
	}

	/**
	 * Sets the request wrapper filter url patterns.
	 *
	 * @param requestWrapperFilterUrlPatterns the request wrapper filter url patterns
	 */
	public void setRequestWrapperFilterUrlPatterns(String[] requestWrapperFilterUrlPatterns) {
		this.requestWrapperFilterUrlPatterns = requestWrapperFilterUrlPatterns;
	}

	/**
	 * Returns the redirect after validation.
	 *
	 * @return the redirect after validation
	 */
	public boolean isRedirectAfterValidation() {
		return redirectAfterValidation;
	}

	/**
	 * Sets the redirect after validation.
	 *
	 * @param redirectAfterValidation the redirect after validation
	 */
	public void setRedirectAfterValidation(boolean redirectAfterValidation) {
		this.redirectAfterValidation = redirectAfterValidation;
	}

	/**
	 * Returns the renew.
	 *
	 * @return the renew
	 */
	public boolean isRenew() {
		return renew;
	}

	/**
	 * Sets the renew.
	 *
	 * @param renew the renew
	 */
	public void setRenew(boolean renew) {
		this.renew = renew;
	}

	/**
	 * Returns the relay state parameter name.
	 *
	 * @return the relay state parameter name
	 */
	public String getRelayStateParameterName() {
		return relayStateParameterName;
	}

	/**
	 * Sets the relay state parameter name.
	 *
	 * @param relayStateParameterName the relay state parameter name
	 */
	public void setRelayStateParameterName(String relayStateParameterName) {
		this.relayStateParameterName = relayStateParameterName;
	}

	/**
	 * Returns the role attribute.
	 *
	 * @return the role attribute
	 */
	public String getRoleAttribute() {
		return roleAttribute;
	}

	/**
	 * Sets the role attribute.
	 *
	 * @param roleAttribute the role attribute
	 */
	public void setRoleAttribute(String roleAttribute) {
		this.roleAttribute = roleAttribute;
	}
	
	/**
	 * Returns the remember me attribute name.
	 *
	 * @return the remember me attribute name
	 */
	public String getRememberMeAttributeName() {
		return rememberMeAttributeName;
	}

	/**
	 * Sets the remember me attribute name.
	 *
	 * @param rememberMeAttributeName the remember me attribute name
	 */
	public void setRememberMeAttributeName(String rememberMeAttributeName) {
		this.rememberMeAttributeName = rememberMeAttributeName;
	}

	/**
	 * Returns the secret key.
	 *
	 * @return the secret key
	 */
	public String getSecretKey() {
		return secretKey;
	}

	/**
	 * Sets the secret key.
	 *
	 * @param secretKey the secret key
	 */
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	/**
	 * Returns the server callback url.
	 *
	 * @return the server callback url
	 */
	public String getServerCallbackUrl() {
		return serverCallbackUrl;
	}

	/**
	 * Sets the server callback url.
	 *
	 * @param serverCallbackUrl the server callback url
	 */
	public void setServerCallbackUrl(String serverCallbackUrl) {
		this.serverCallbackUrl = serverCallbackUrl;
	}

	/**
	 * Returns the server name.
	 *
	 * @return the server name
	 */
	public String getServerName() {
		return serverName;
	}

	/**
	 * Sets the server name.
	 *
	 * @param serverName the server name
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	
	/**
	 * Returns the service.
	 *
	 * @return the service
	 */
	public String getService() {
		return service;
	}

	/**
	 * Sets the service.
	 *
	 * @param service the service
	 */
	public void setService(String service) {
		this.service = service;
	}

	/**
	 * Returns the sign out filter url patterns.
	 *
	 * @return the sign out filter url patterns
	 */
	public String[] getSignOutFilterUrlPatterns() {
		return signOutFilterUrlPatterns;
	}

	/**
	 * Sets the sign out filter url patterns.
	 *
	 * @param signOutFilterUrlPatterns the sign out filter url patterns
	 */
	public void setSignOutFilterUrlPatterns(String[] signOutFilterUrlPatterns) {
		this.signOutFilterUrlPatterns = signOutFilterUrlPatterns;
	}

	/**
	 * Returns the ssl config file.
	 *
	 * @return the ssl config file
	 */
	public String getSslConfigFile() {
		return sslConfigFile;
	}

	/**
	 * Sets the ssl config file.
	 *
	 * @param sslConfigFile the ssl config file
	 */
	public void setSslConfigFile(String sslConfigFile) {
		this.sslConfigFile = sslConfigFile;
	}

	/**
	 * Returns the ticket validation filter url patterns.
	 *
	 * @return the ticket validation filter url patterns
	 */
	public String[] getTicketValidationFilterUrlPatterns() {
		return ticketValidationFilterUrlPatterns;
	}

	/**
	 * Sets the ticket validation filter url patterns.
	 *
	 * @param ticketValidationFilterUrlPatterns the ticket validation filter url patterns
	 */
	public void setTicketValidationFilterUrlPatterns(String[] ticketValidationFilterUrlPatterns) {
		this.ticketValidationFilterUrlPatterns = ticketValidationFilterUrlPatterns;
	}

	/**
	 * Returns the ticket validator class.
	 *
	 * @return the ticket validator class
	 */
	public String getTicketValidatorClass() {
		return ticketValidatorClass;
	}

	/**
	 * Sets the ticket validator class.
	 *
	 * @param ticketValidatorClass the ticket validator class
	 */
	public void setTicketValidatorClass(String ticketValidatorClass) {
		this.ticketValidatorClass = ticketValidatorClass;
	}

	/**
	 * Returns the tolerance.
	 *
	 * @return the tolerance
	 */
	public long getTolerance() {
		return tolerance;
	}

	/**
	 * Sets the tolerance.
	 *
	 * @param tolerance the tolerance
	 */
	public void setTolerance(long tolerance) {
		this.tolerance = tolerance;
	}

	/**
	 * Returns the use session.
	 *
	 * @return the use session
	 */
	public boolean isUseSession() {
		return useSession;
	}

	/**
	 * Sets the use session.
	 *
	 * @param useSession the use session
	 */
	public void setUseSession(boolean useSession) {
		this.useSession = useSession;
	}

	/**
	 * Returns the service parameter name.
	 *
	 * @return the service parameter name
	 */
	public String getServiceParameterName() {
		return serviceParameterName;
	}

	/**
	 * Sets the service parameter name.
	 *
	 * @param serviceParameterName the service parameter name
	 */
	public void setServiceParameterName(String serviceParameterName) {
		this.serviceParameterName = serviceParameterName;
	}

}

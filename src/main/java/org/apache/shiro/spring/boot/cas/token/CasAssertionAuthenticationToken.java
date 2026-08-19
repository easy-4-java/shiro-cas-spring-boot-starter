/*
 * Copyright (c) 2017, hiwepy (https://github.com/hiwepy).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.shiro.spring.boot.cas.token;

import java.util.Map;

import org.apache.shiro.biz.authc.token.LoginProtocolAuthenticationToken;
import org.apache.shiro.biz.authc.token.LoginType;
import org.apache.shiro.biz.authc.token.LoginTypeAuthenticationToken;
import org.apache.shiro.biz.authc.token.UsernameWithoutPwdToken;
import org.apereo.cas.client.validation.Assertion;

/**
 * <p>Token for Cas Assertion Authentication.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@SuppressWarnings("serial")
public class CasAssertionAuthenticationToken extends UsernameWithoutPwdToken implements LoginTypeAuthenticationToken, LoginProtocolAuthenticationToken {

	/** The service ticket returned by the CAS server */
    private String ticket;
	private Assertion assertion;
	/** 登陆IP */
	private String host;
	/** 登陆协议 */
	private String protocol;

	/**
	 * Constructs a new cas assertion authentication token instance.
	 *
	 * @param ticket the ticket
	 * @param host the host
	 */
	public CasAssertionAuthenticationToken(String ticket, String host) {
		this.ticket = ticket;
		this.host = host;
		this.setHost(host);
	}

	/**
	 * Sets the assertion.
	 *
	 * @param assertion the assertion
	 */
	public void setAssertion(Assertion assertion) {
		this.assertion = assertion;
	}

	/**
	 * Returns the assertion.
	 *
	 * @return the assertion
	 */
	public Assertion getAssertion() {
		return assertion;
	}

	/**
	 * Returns the credentials.
	 *
	 * @return the credentials
	 */
	@Override
	public Object getCredentials() {
		return ticket;
	}

	/**
	 * Returns the host.
	 *
	 * @return the host
	 */
	@Override
	public String getHost() {
		return host;
	}

	/**
	 * Returns the login type.
	 *
	 * @return the login type
	 */
	@Override
	public LoginType getLoginType() {
		return LoginType.CAS;
	}

	/**
	 * Returns the protocol.
	 *
	 * @return the protocol
	 */
	@Override
	public String getProtocol() {
		return protocol;
	}

	/**
	 * Sets the protocol.
	 *
	 * @param protocol the protocol
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	/**
	 * Sets the ticket.
	 *
	 * @param ticket the ticket
	 */
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	/**
	 * Returns the ticket.
	 *
	 * @return the ticket
	 */
	public String getTicket() {
		return ticket;
	}

}

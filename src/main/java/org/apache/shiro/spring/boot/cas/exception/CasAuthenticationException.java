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
package org.apache.shiro.spring.boot.cas.exception;

import org.apache.shiro.authc.AuthenticationException;

/**
 * Exception thrown when CAS authentication fails.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class CasAuthenticationException extends AuthenticationException {

    /**
     * Constructs a new cas authentication exception instance.
     *
     */
    public CasAuthenticationException() {
        super();
    }

    /**
     * Constructs a new cas authentication exception instance.
     *
     * @param message the message
     */
    public CasAuthenticationException(String message) {
        super(message);
    }

    /**
     * Constructs a new cas authentication exception instance.
     *
     * @param cause the cause
     */
    public CasAuthenticationException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new cas authentication exception instance.
     *
     * @param message the message
     * @param cause the cause
     */
    public CasAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}

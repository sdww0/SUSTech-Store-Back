package com.susstore.config.security.exception;

import org.springframework.security.core.AuthenticationException;

public class UserHasBannedException extends AuthenticationException {

    public UserHasBannedException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public UserHasBannedException(String msg) {
        super(msg);
    }
}

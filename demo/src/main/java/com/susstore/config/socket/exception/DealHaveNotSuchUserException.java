package com.susstore.config.socket.exception;

import org.springframework.security.core.AuthenticationException;

public class DealHaveNotSuchUserException extends AuthenticationException {

    public DealHaveNotSuchUserException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public DealHaveNotSuchUserException(String msg) {
        super(msg);
    }

}

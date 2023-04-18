/*
 * Do not reproduce without permission in writing.
 * Copyright (c) 2023.
 */
package com.khomenko.learn.applicationweb.utils.exception;

public class ApplicationBusinessException extends RuntimeException {

    public ApplicationBusinessException(String message) {
        super(message);
    }

    public ApplicationBusinessException(Throwable cause) {
        super(cause);
    }

    public ApplicationBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

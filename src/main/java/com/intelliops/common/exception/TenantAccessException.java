package com.intelliops.common.exception;

public class TenantAccessException extends RuntimeException {
    public TenantAccessException(String message) {
        super(message);
    }
}

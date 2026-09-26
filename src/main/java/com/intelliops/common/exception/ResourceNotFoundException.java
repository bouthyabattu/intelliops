package com.intelliops.common.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
    public ResourceNotFoundException(String resource, Object id) {
        super(resource + " not found with id: " + id);
    }
}

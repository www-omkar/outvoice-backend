package com.omkarsathe.outvoice.common.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class ApiError {
    private final int status;
    private final String message;
    private final List<String> errors;

    public ApiError(int status, String message, List<String> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public ApiError(int status, String message) {
        this(status, message, List.of());
    }
}

package com.practice.springbatch_practice1.config.repeatanderrorhandling.retry;

public class RetryableException extends RuntimeException {

    public RetryableException(String message) {
        super(message);
    }

    public RetryableException() {
    }
}

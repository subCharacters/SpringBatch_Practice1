package com.practice.springbatch_practice1.config.listener.retrylistener;

public class CustomRetryException extends RuntimeException {
    public CustomRetryException(String message) {
        super(message);
    }
}

package com.practice.springbatch_practice1;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @EnableBatchProcessing // v4까지 필수. v5에선 필요없음.
@SpringBootApplication
public class SpringBatchPractice1Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchPractice1Application.class, args);
    }

}

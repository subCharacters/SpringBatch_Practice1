package com.practice.springbatch_practice1;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing // 배치 프로그램이라는걸 알리는 어노테이션. 추가 필수
@SpringBootApplication
public class SpringBatchPractice1Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchPractice1Application.class, args);
    }

}

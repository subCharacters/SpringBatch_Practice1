package com.practice.springbatch_practice1.config.test;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

// 이 설정들이 없으면 빈 주입이 제대로 안됨
@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration
public class TestBatchConfig {
}

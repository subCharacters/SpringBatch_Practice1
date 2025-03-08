package com.practice.springbatch_practice1.config.repeatanderrorhandling.retry.template;

import com.practice.springbatch_practice1.config.repeatanderrorhandling.retry.RetryableException;
import com.practice.springbatch_practice1.config.repeatanderrorhandling.retry.api.RetryItemProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class RetryTemplateConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public RetryTemplateConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job retryTemplateJob() {
        return new JobBuilder("retryTemplateJob", jobRepository)
                .start(retryTemplateStep())
                .incrementer(new RunIdIncrementer())
                .build();

    }

    @Bean
    public Step retryTemplateStep() {
        return new StepBuilder("retryTemplateStep", jobRepository)
                .<String, String>chunk(10, transactionManager)
                .reader(retryTemplateReader())
                .processor(retryTemplatePolicyProcessor())
                .writer(chunk -> chunk.getItems().forEach(System.out::println))
                .faultTolerant()

                .build();
    }

    @Bean
    public ItemProcessor<? super String, String> retryTemplatePolicyProcessor() {

        return new RetryTemplateItemProcessor();
    }

    @Bean
    public ItemReader<String> retryTemplateReader() {
        List<String> items = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            items.add(String.valueOf(i));
        }

        return new ListItemReader<>(items);
    }

    @Bean
    public RetryPolicy retryTemplatePolicy() {
        Map<Class<? extends Throwable>, Boolean> exceptionClass = new HashMap<>();
        exceptionClass.put(RetryableException.class, true);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(2, exceptionClass);

        return retryPolicy;
    }

    @Bean
    public RetryTemplate retryTemplate() {
        Map<Class<? extends Throwable>, Boolean> exceptionClass = new HashMap<>();
        exceptionClass.put(RetryableException.class, true); // retry 할 예외 클래스 설정

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(2000); // 2초간 retry 대기

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(2, exceptionClass); // Policy 설정
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(retryPolicy); // 템플릿에 policy 설정
        retryTemplate.setBackOffPolicy(backOffPolicy); // back off policy 설정

        return retryTemplate;
    }
}

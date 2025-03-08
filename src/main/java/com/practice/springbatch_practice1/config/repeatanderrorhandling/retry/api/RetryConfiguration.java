package com.practice.springbatch_practice1.config.repeatanderrorhandling.retry.api;

import com.practice.springbatch_practice1.config.repeatanderrorhandling.retry.RetryableException;
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
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Retry가 활성화 되면 RepeatTemplate반복에서 RetryTemplate반복으로 바뀐다.
 */
@Configuration
public class RetryConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public RetryConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job retryJob() {
        return new JobBuilder("retryJob", jobRepository)
                .start(retryStep())
                .incrementer(new RunIdIncrementer())
                .build();

    }

    @Bean
    public Step retryStep() {
        return new StepBuilder("retryStep", jobRepository)
                .<String, String>chunk(10, transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(chunk -> chunk.getItems().forEach(System.out::println))
                .faultTolerant()
                // retry도 청크를 처음부터 다시 시작함.
                // 근데 skip처럼 건너띄는게 아니라 처리를 처음부터 다시 하므로 에러가 난 아이템을
                // 무시하고 retry하려면 skip을 설정해주는게 좋음. 근데 리트라이 횟수를 다 써버리는 단점.
                // 리트라이 후 리커버리에서 skip할지말지를 판단함.
                .skip(RetryableException.class)
                .skipLimit(2)
                // .retry(RetryableException.class)
                // .retryLimit(2)
                .retryPolicy(retryPolicy())
                .build();
    }

    @Bean
    public ItemProcessor<? super String, String> processor() {

        return new RetryItemProcessor();
    }

    @Bean
    public ItemReader<String> reader() {
        List<String> items = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            items.add(String.valueOf(i));
        }

        return new ListItemReader<>(items);
    }

    @Bean
    public RetryPolicy retryPolicy() {
        Map<Class<? extends Throwable>, Boolean> exceptionClass = new HashMap<>();
        exceptionClass.put(RetryableException.class, true);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(2, exceptionClass);

        return retryPolicy;
    }
}

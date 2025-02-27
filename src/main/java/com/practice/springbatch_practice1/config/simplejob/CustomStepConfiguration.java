package com.practice.springbatch_practice1.config.simplejob;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 커스텀으로 구현한 클래스
 */
@Component
public class CustomStepConfiguration {

    private final JobRepository jobRepository;
    public CustomStepConfiguration(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Bean
    public Step sampleStep(Tasklet startNextTestTasklet, PlatformTransactionManager transactionManager) {
        return new StepBuilder("sampleStep", jobRepository)
                .tasklet(startNextTestTasklet, transactionManager)
                .build();
    }
}

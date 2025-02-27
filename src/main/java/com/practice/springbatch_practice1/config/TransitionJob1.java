package com.practice.springbatch_practice1.config;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class TransitionJob1 {

    @Bean
    public Job batchTransitionJob1(JobRepository jobRepository, Step transitionJob1Step1, Step transitionJob1Step2) {
        return new JobBuilder("batchTransitionJob1", jobRepository)
                .start(transitionJob1Step1)
                .next(transitionJob1Step2)
                .build();
    }

    @Bean
    public Step transitionJob1Step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("transitionJob1Step1", jobRepository)
                .tasklet(transitionTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Step transitionJob1Step2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("transitionJob1Step2", jobRepository)
                .tasklet(transitionTasklet(), transactionManager)
                .build();
    }

    public Tasklet transitionTasklet() {
        return (contribution, chunkContext) -> {
            contribution.setExitStatus(ExitStatus.FAILED);
            return RepeatStatus.FINISHED;
        };
    }
}

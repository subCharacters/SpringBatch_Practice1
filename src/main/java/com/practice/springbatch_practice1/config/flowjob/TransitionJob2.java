package com.practice.springbatch_practice1.config.flowjob;

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
public class TransitionJob2 {

    @Bean
    public Job TransitionJob2(JobRepository jobRepository, Step transitionJob2Step1, Step transitionJob2Step2) {
        return new JobBuilder("batchTransitionJob2", jobRepository)
                .start(transitionJob2Step1)
                .next(transitionJob2Step2)
                .build();
    }

    @Bean
    public Step transitionJob2Step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("transitionJob2Step1", jobRepository)
                .tasklet(transitionTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Step transitionJob2Step2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("transitionJob2Step2", jobRepository)
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

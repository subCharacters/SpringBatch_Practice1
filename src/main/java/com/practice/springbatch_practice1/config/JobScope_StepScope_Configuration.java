package com.practice.springbatch_practice1.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class JobScope_StepScope_Configuration {

    @Bean
    public Job batchJobScope_StepScopeJob(JobRepository jobRepository
            , Step jobScope_StepScopeStep1
            , Step jobScope_StepScopeStep2) {
        return new JobBuilder("batchJobScope_StepScopeJob", jobRepository)
                .start(jobScope_StepScopeStep1)
                .next(jobScope_StepScopeStep2)
                .listener(new CustomBatchJobScope_StepScopeListenerJob())
                .build();
    }

    @Bean
    @JobScope
    public Step jobScope_StepScopeStep1(
            JobRepository jobRepository, PlatformTransactionManager transactionManager
            , Tasklet jobScope_StepScopeTasklet
            , @Value("#{jobParameters['message']}") String message) {
        System.out.println("message = " + message);
        return new StepBuilder("jobScope_StepScopeStep1", jobRepository)
                .tasklet(jobScope_StepScopeTasklet, transactionManager)
                .listener(new CustomBatchJobScope_StepScopeListenerStep())
                .build();
    }

    @Bean
    public Step jobScope_StepScopeStep2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("jobScope_StepScopeStep2", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 2 executed");
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet jobScope_StepScopeTasklet(@Value("#{jobExecutionContext['name']}") String name
    , @Value("#{stepExecutionContext['name2']}") String name2) {
        System.out.println("name = " + name + ", name2 = " + name2);
        return ((contribution, chunkContext) -> {
            System.out.println("jobScope_StepScopeTasklet executed");
            return RepeatStatus.FINISHED;
        });
    }
}

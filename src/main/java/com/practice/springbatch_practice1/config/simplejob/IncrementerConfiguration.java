package com.practice.springbatch_practice1.config.simplejob;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class PreventRestartConfiguration {

    @Bean
    public Job batchJob1(JobRepository jobRepository, Step step1, Step step2, Step step3) {
        return new JobBuilder("batchJob1", jobRepository)
                .start(step1)
                .next(step2)
                .next(step3)
                .preventRestart() // job이 실패해서 재시작이 되어야 하지만 재시작을 못한다.
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, Tasklet testTasklet,PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .tasklet(testTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step step2(JobRepository jobRepository, Tasklet testTasklet,PlatformTransactionManager transactionManager) {
        return new StepBuilder("step2", jobRepository)
                .tasklet(testTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step step3(JobRepository jobRepository, Tasklet failTasklet, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step3", jobRepository)
                .tasklet(failTasklet, transactionManager)
                .build();
    }

    @Bean
    public Tasklet testTasklet() {
        return ((contribution, chunkContext) -> {
            String stepName = chunkContext.getStepContext().getStepName();
            System.out.println(stepName + " is execute");
            return RepeatStatus.FINISHED;
        });
    }

    @Bean
    public Tasklet failTasklet() {
        return ((contribution, chunkContext) -> {
            throw new RuntimeException("step3 was failed");
        });
    }
}

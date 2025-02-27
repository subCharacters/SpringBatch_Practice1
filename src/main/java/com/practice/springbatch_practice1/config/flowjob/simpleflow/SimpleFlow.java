package com.practice.springbatch_practice1.config.flowjob.simpleflow;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class SimpleFlow {

    @Bean
    public Job batchSimpleFlowJob(JobRepository jobRepository
            , Step templateJobStep1
            , Step templateJobStep2
            , Step templateJobStep3
            , Step templateJobStep4
            , Step templateJobStep5) {
        return new JobBuilder("batchSimpleFlowJob", jobRepository)
                .start(templateJobStep1)
                .next(templateJobStep2)
                .build();
    }

    @Bean
    public Step simpleFlowStep1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("simpleFlowStep1", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 1 executed");
                    contribution.setExitStatus(ExitStatus.FAILED); // exit status를 정의
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }

    @Bean
    public Step simpleFlowStep2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("simpleFlowStep2", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 2 executed");
                    contribution.setExitStatus(ExitStatus.COMPLETED);
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }

    @Bean
    public Step simpleFlowStep3(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("simpleFlowStep3", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 3 executed");
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }

    @Bean
    public Step simpleFlowStep4(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("simpleFlowStep4", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 4 executed");
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }

    @Bean
    public Step simpleFlowStep5(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("simpleFlowStep5", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 5 executed");
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }
}

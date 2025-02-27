package com.practice.springbatch_practice1.batch;

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
public class TemplateJob {

    @Bean
    public Job batchTemplateJob(JobRepository jobRepository
            , Step templateJobStep1
            , Step templateJobStep2
            , Step templateJobStep3
            , Step templateJobStep4
            , Step templateJobStep5) {
        return new JobBuilder("batchTemplateJob", jobRepository)
                .start(templateJobStep1)
                .next(templateJobStep2)
                .build();
    }

    @Bean
    public Step templateJobStep1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("templateJobStep1", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 1 executed");
                    contribution.setExitStatus(ExitStatus.FAILED); // exit status를 정의
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }

    @Bean
    public Step templateJobStep2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("templateJobStep2", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 2 executed");
                    contribution.setExitStatus(ExitStatus.COMPLETED);
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }

    @Bean
    public Step templateJobStep3(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("templateJobStep3", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 3 executed");
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }

    @Bean
    public Step templateJobStep4(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("templateJobStep4", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 4 executed");
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }

    @Bean
    public Step templateJobStep5(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("templateJobStep5", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 5 executed");
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }
}

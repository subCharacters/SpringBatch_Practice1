package com.practice.springbatch_practice1.config.joboperator;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class JobOperationConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public JobOperationConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job jobOperationJob() {
        return new JobBuilder("jobOperationJob", jobRepository)
                .start(jobOperationStep())
                .next(jobOperationStep2())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step jobOperationStep() {
        return new StepBuilder("jobOperationStep",jobRepository)
                .tasklet(jobOperationTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Tasklet jobOperationTasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("jobOperationTasklet");
                Thread.sleep(5000);
                return RepeatStatus.FINISHED;
            }
        };
    }

    @Bean
    public Step jobOperationStep2() {
        return new StepBuilder("jobOperationStep2",jobRepository)
                .tasklet(jobOperationTasklet2(), transactionManager)
                .build();
    }

    @Bean
    public Tasklet jobOperationTasklet2() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("jobOperationTasklet2");
                Thread.sleep(5000);
                return RepeatStatus.FINISHED;
            }
        };
    }
}

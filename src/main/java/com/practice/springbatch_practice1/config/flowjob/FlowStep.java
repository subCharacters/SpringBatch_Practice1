package com.practice.springbatch_practice1.config.flowjob;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class FlowStep {

    @Bean
    public Job batchFlowStepJob(JobRepository jobRepository
            , Step flowStepStep
            , Step flowStepStep2) {
        return new JobBuilder("batchFlowStepJob", jobRepository)
                .start(flowStepStep)
                .next(flowStepStep2)
                .build();
    }

    @Bean
    public Step flowStepStep(JobRepository jobRepository, Flow flowStepFlow) {
        return new StepBuilder("flowStepStep", jobRepository)
                .flow(flowStepFlow)
                .build();
    }

    @Bean
    public Flow flowStepFlow(Step flowStepStep1) {
        FlowBuilder<Flow> flowStepFlow = new FlowBuilder<>("flowStepFlow");
        flowStepFlow
                .start(flowStepStep1)
                .end();

        return flowStepFlow.build();
    }

    @Bean
    public Step flowStepStep1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("flowStepStep1", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 1 executed");
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }

    @Bean
    public Step flowStepStep2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("flowStepStep2", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 2 executed");
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }
}

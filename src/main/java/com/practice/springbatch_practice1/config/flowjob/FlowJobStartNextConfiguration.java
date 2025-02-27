package com.practice.springbatch_practice1.config.flowjob;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class FlowJobStartNextConfiguration {

    @Bean
    public Job batchFlowJobStartNextJob(JobRepository jobRepository, Flow flowJobStartNextFlow1
            , Flow flowJobStartNextFlow2 ,Step flowJobStartNextStep3, Step flowJobStartNextStep6) {
        return new JobBuilder("batchFlowJobStartNextJob", jobRepository)
                .start(flowJobStartNextFlow1)
                .next(flowJobStartNextStep3)
                .next(flowJobStartNextFlow2)
                .next(flowJobStartNextStep6)
                .end()
                .build();
    }

    @Bean
    public Flow flowJobStartNextFlow1(Step flowJobStartNextStep1, Step flowJobStartNextStep2) {
        return new FlowBuilder<Flow>("flowJobStartNextFlow1")
                .start(flowJobStartNextStep1)
                .next(flowJobStartNextStep2)
                .end();
    }

    @Bean
    public Flow flowJobStartNextFlow2(Step flowJobStartNextStep4, Step flowJobStartNextStep5) {
        return new FlowBuilder<Flow>("flowJobStartNextFlow1")
                .start(flowJobStartNextStep4)
                .next(flowJobStartNextStep5)
                .end();
    }

    @Bean
    public Step flowJobStartNextStep1(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("flowJobStartNextStep1", jobRepository)
                .tasklet(flowJobStartNextTasklet1(), platformTransactionManager)
                .build();
    }

    @Bean
    public Step flowJobStartNextStep2(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("flowJobStartNextStep2", jobRepository)
                .tasklet(flowJobStartNextTasklet1(), platformTransactionManager)
                .build();
    }

    @Bean
    public Step flowJobStartNextStep3(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("flowJobStartNextStep3", jobRepository)
                .tasklet(flowJobStartNextTasklet1(), platformTransactionManager)
                .build();
    }

    @Bean
    public Step flowJobStartNextStep4(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("flowJobStartNextStep4", jobRepository)
                .tasklet(flowJobStartNextTasklet1(), platformTransactionManager)
                .build();
    }

    @Bean
    public Step flowJobStartNextStep5(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("flowJobStartNextStep5", jobRepository)
                .tasklet(flowJobStartNextTasklet1(), platformTransactionManager)
                .build();
    }

    @Bean
    public Step flowJobStartNextStep6(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("flowJobStartNextStep6", jobRepository)
                .tasklet(flowJobStartNextTasklet1(), platformTransactionManager)
                .build();
    }

    public Tasklet flowJobStartNextTasklet1() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                String stepName = contribution.getStepExecution().getStepName();
                System.out.println(stepName + " has executed");
                // throw new RuntimeException("fail");
                return RepeatStatus.FINISHED;
            }
        };
    }
}

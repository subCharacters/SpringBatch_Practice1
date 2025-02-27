package com.practice.springbatch_practice1.config.flowjob;

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
public class FlowJobCustomExitStatusConfiguration {

    @Bean
    public Job batchFlowJobCustomExitStatusJob(JobRepository jobRepository
            , Step flowJobCustomExitStatusJobStep1
            , Step flowJobCustomExitStatusJobStep2) {
        return new JobBuilder("batchFlowJobCustomExitStatusJob", jobRepository)
                .start(flowJobCustomExitStatusJobStep1)
                    .on("FAILED")
                    .to(flowJobCustomExitStatusJobStep2)
                    .on("PASS")
                    .stop()
                .end()
                .build();
    }

    @Bean
    public Step flowJobCustomExitStatusJobStep1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("flowJobCustomExitStatusJobStep1", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 1 executed");
                    contribution.setExitStatus(ExitStatus.FAILED); // exit status를 정의
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }

    @Bean
    public Step flowJobCustomExitStatusJobStep2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("flowJobCustomExitStatusJobStep2", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 2 executed");
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .listener(new CustomExitStatus()) // 리스너를 통해 새로 exit status code를 정의
                .build();
    }
}

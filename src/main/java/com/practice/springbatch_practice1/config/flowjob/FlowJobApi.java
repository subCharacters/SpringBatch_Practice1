package com.practice.springbatch_practice1.config.flowjob;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
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
public class FlowJobApi {

    @Bean
    public Job batchFlowJobApi(JobRepository jobRepository, Step flowJobApiStep1, Step flowJobApiStep2 ,Step flowJobApiStep3) {
        return new JobBuilder("flowJobApi", jobRepository)
                .start(flowJobApiStep1)
                    .on("COMPLETED").to(flowJobApiStep3) // flowJobApiStep1이 성공하면 flowJobApiStep3로 가고
                .from(flowJobApiStep1).on("FAILED").to(flowJobApiStep2) // flowJobApiStep1이 실패하면 flowJobApiStep2로 가라
                .end()
                .build();
    }

    @Bean
    public Step flowJobApiStep1(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("flowJobApiStep1", jobRepository)
                .tasklet(flowJobTasklet1(), platformTransactionManager)
                .build();
    }

    @Bean
    public Step flowJobApiStep2(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("flowJobApiStep2", jobRepository)
                .tasklet(flowJobTasklet2(), platformTransactionManager)
                .build();
    }

    @Bean
    public Step flowJobApiStep3(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("flowJobApiStep3", jobRepository)
                .tasklet(flowJobTasklet3(), platformTransactionManager)
                .build();
    }

    public Tasklet flowJobTasklet1() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("flowJobTasklet1 has executed");
                // throw new RuntimeException("fail");
                return RepeatStatus.FINISHED;
            }
        };
    }

    public Tasklet flowJobTasklet2() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("flowJobTasklet2 has executed");
                return RepeatStatus.FINISHED;
            }
        };
    }

    public Tasklet flowJobTasklet3() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("flowJobTasklet3 has executed");
                return RepeatStatus.FINISHED;
            }
        };
    }
}

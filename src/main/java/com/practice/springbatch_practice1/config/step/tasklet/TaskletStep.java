package com.practice.springbatch_practice1.config.step.tasklet;

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
public class TaskletStep {

    @Bean
    public Job taskletStepJob(JobRepository jobRepository, Step taskletStepStep) {
        return new JobBuilder("taskletStepJob", jobRepository)
                .start(taskletStepStep)
                .build();
    }

    @Bean
    public Step taskletStepStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                Tasklet taskletStepTaskletLambda) {
        return new StepBuilder("taskletStepStep", jobRepository)
                .tasklet(taskletStepTasklet(), transactionManager)
                .tasklet(taskletStepTaskletLambda, transactionManager)
                .tasklet(new CustomTasklet(), transactionManager)
                .build();
    }

    public Tasklet taskletStepTasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("taskletStepTasklet");
                return RepeatStatus.FINISHED;
            }
        };
    }

    // Step의 인수로 전달해서 사용할 경우 빈 등록이 필요하다.
    @Bean
    public Tasklet taskletStepTaskletLambda() {
        return ((contribution, chunkContext) -> {
            System.out.println("taskletStepTaskletLambda");
            return RepeatStatus.FINISHED;
        });
    }

    public class CustomTasklet implements Tasklet {

        @Override
        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
            System.out.println("CustomTasklet");
            return RepeatStatus.FINISHED;
        }
    }

}

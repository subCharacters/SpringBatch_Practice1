package com.practice.springbatch_practice1.config.step.tasklet;

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
public class StartlimitAllowStartIfCompleteStep {

    @Bean
    public Job startlimitAllowStartIfCompleteStepJob(JobRepository jobRepository,
                                                     Step allowStartIfCompleteStepStep,
                                                     Step startlimitStepStep) {
        return new JobBuilder("StartlimitAllowStartIfCompleteStepJob", jobRepository)
                .start(allowStartIfCompleteStepStep)
                .start(startlimitStepStep)
                .build();
    }

    @Bean
    public Step allowStartIfCompleteStepStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("allowStartIfCompleteStepStep", jobRepository)
                .tasklet(allowStartIfCompleteStepTasklet(), transactionManager)
                .allowStartIfComplete(true) // 항상 step실행이 된다.
                .build();
    }

    @Bean
    public Step startlimitStepStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("startlimitStepStep", jobRepository)
                .tasklet(startlimitStepTasklet(), transactionManager)
                .startLimit(3) // 4번째 시작시 limit 예외가 발생한다.
                .build();
    }

    public Tasklet allowStartIfCompleteStepTasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("allowStartIfCompleteStepTasklet");
                return RepeatStatus.FINISHED;
            }
        };
    }

    public Tasklet startlimitStepTasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("startlimitStepTasklet");
                throw new RuntimeException("");
                //return RepeatStatus.FINISHED;
            }
        };
    }
}

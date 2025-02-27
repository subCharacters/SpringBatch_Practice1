package com.practice.springbatch_practice1.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class TaskletJob
{

    /* Spring framework 6기반의 Spring batch5는 jdk17이상 필수.
    * 아래 클래스는 더이상 사용되지 않음. v4까지 v5부터..
    * https://github.com/spring-projects/spring-batch/wiki/Spring-Batch-5.0-Migration-Guide
    * */
    /*
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job tasklet_job() {
        return jobBuilderFactory.get("tasklet_job")
            .start(tasklet_step()).build();
    }

    @Bean
    public Step tasklet_step() {
        return stepBuilderFactory.get("tasklet_step")
                .tasklet(
                        (a, b) -> {
                            System.out.println("-> job -> [step]");
                            return RepeatStatus.FINISHED;
                        }
                ).build():
    }
    */

    @Bean
    public Job tasklet_job(JobRepository jobRepository, Step tasklet_step) {
        return new JobBuilder("tasklet_job", jobRepository)
                .start(tasklet_step)
                .build();
    }

    @Bean
    public FirstTasklet firstTasklet() {
        FirstTasklet firstTasklet = new FirstTasklet();
        firstTasklet.setMsg("-> job -> [step]");
        return firstTasklet;
    }

    @Bean
    public Step tasklet_step(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        // log.debug("tasklet_step");
        // System.out.println("tasklet_step");
        return new StepBuilder("tasklet_step", jobRepository)
                .tasklet(firstTasklet(), transactionManager)
                .allowStartIfComplete(true) // job재실행 시 성공적으로 끝나고 재실행에 포함.
                .build();
    }
}

package com.practice.springbatch_practice1.config.thread.parallel;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ParallelStepsConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public ParallelStepsConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job parallelStepsJob() {
        return new JobBuilder("parallelStepsJob", jobRepository)
                .start(parallelStepsStep1())
                .split(taskExecutor()).add(parallelStepsStep2()).end()
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step parallelStepsStep1() {
        return new StepBuilder("parallelStepsStep1", jobRepository)
                .tasklet(parallelStepsTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Flow parallelStepsStep2() {
        Step step2 = new StepBuilder("step2", jobRepository)
                .tasklet(parallelStepsTasklet(), transactionManager).build();

        Step step3 = new StepBuilder("step3", jobRepository)
                .tasklet(parallelStepsTasklet(), transactionManager).build();

        return new FlowBuilder<Flow>("parallelStepsStep1")
                .start(step2)
                .next(step3)
                .build();
    }

    @Bean
    public Tasklet parallelStepsTasklet() {
        return new CustomParallelStepsTasklet();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(4);
        threadPoolTaskExecutor.setMaxPoolSize(8);
        threadPoolTaskExecutor.setThreadNamePrefix("parallelStepsJob-");
        return threadPoolTaskExecutor;
    }
}

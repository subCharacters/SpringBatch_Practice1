package com.practice.springbatch_practice1.config.thread.parallel;

import com.practice.springbatch_practice1.config.flowjob.simpleflow.SimpleFlow;
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
                // parallelStepsStep1과 parallelStepsStep2에서 step1과 step2는 동시에 움직이고 step2 -> step3순으로 돌아간다.
                .start(parallelStepsStep1())
                // parallelStepsStep1가 먼저 실행된 뒤 step2와 step3을 실행 시킨고 싶다면 아래와 같은 로직으로 구성.
                /*
                .next(
                        new StepBuilder("parallelStepsStep", jobRepository)
                                .flow(new FlowBuilder<Flow>("parallelStepsStepFlow").split(taskExecutor())
                                        .add(parallelStep2Flow(), parallelStep3Flow()).end())
                                .build()
                )
                */
                .split(taskExecutor()).add(parallelStepsStep2())
                .end()
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

    // 여기서부터
    @Bean
    public Flow parallelStep2Flow() {
        Step step = new StepBuilder("parallelStep2FlowStep1", jobRepository)
                .tasklet(parallelStepsTasklet(), transactionManager).build();
        return new FlowBuilder<Flow>("parallelStep2Flow")
                .start(step)
                .build();
    }

    @Bean
    public Flow parallelStep3Flow() {
        Step step = new StepBuilder("parallelStep2FlowStep2", jobRepository)
                .tasklet(parallelStepsTasklet(), transactionManager).build();
        return new FlowBuilder<Flow>("parallelStep3Flow")
                .start(step)
                .build();
    }
    // 여기까지가 step 동시 실행하는 flow부분 step1 -> step2, step3

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

package com.practice.springbatch_practice1.config.simplejob;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class IncrementerConfiguration {

    @Bean
    public Job incrementerJob(JobRepository jobRepository, Step incrementerStep1, Step incrementerStep2, Step incrementerStep3) {
        return new JobBuilder("incrementerJob", jobRepository)
                .start(incrementerStep1)
                .next(incrementerStep2)
                .next(incrementerStep3)
                .incrementer(new RunIdIncrementer()) // 스프링 배치에서 제공해주는 클래스를 이용할 경우
                .incrementer(new CustomJobParametersIncrementer()) // 직접 만들경우.
                .build();
    }

    @Bean
    public Step incrementerStep1(JobRepository jobRepository, Tasklet incrementerTestTasklet,PlatformTransactionManager transactionManager) {
        return new StepBuilder("incrementerStep1", jobRepository)
                .tasklet(incrementerTestTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step incrementerStep2(JobRepository jobRepository, Tasklet incrementerTestTasklet,PlatformTransactionManager transactionManager) {
        return new StepBuilder("incrementerStep2", jobRepository)
                .tasklet(incrementerTestTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step incrementerStep3(JobRepository jobRepository, Tasklet incrementerTestTasklet, PlatformTransactionManager transactionManager) {
        return new StepBuilder("incrementerStep3", jobRepository)
                .tasklet(incrementerTestTasklet, transactionManager)
                .build();
    }

    @Bean
    public Tasklet incrementerTestTasklet() {
        return ((contribution, chunkContext) -> {
            String stepName = chunkContext.getStepContext().getStepName();
            System.out.println(stepName + " is execute");
            return RepeatStatus.FINISHED;
        });
    }
}

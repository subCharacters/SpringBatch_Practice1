package com.practice.springbatch_practice1.config.simplejob;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ValidatorConfiguration {

    @Bean
    public Job validatorJob(JobRepository jobRepository, Step step1, Step step2, Step step3) {
        return new JobBuilder("validatorJob", jobRepository)
                .start(step1)
                .next(step2)
                .next(step3)
                .validator(new CustomJobParametersValidator()) // 커스텀 할 경우.
                // .validator(new DefaultJobParametersValidator(new String[]{"name", "date"}, new String[]{"count"})) // 스프링 배치에서 제공하는 클래스에 구현 할 경우.
                .build();
    }

    @Bean
    public Step validatorStep1(JobRepository jobRepository, Tasklet testTasklet,PlatformTransactionManager transactionManager) {
        return new StepBuilder("validatorStep1", jobRepository)
                .tasklet(testTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step validatorStep2(JobRepository jobRepository, Tasklet testTasklet,PlatformTransactionManager transactionManager) {
        return new StepBuilder("validatorStep2", jobRepository)
                .tasklet(testTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step validatorStep3(JobRepository jobRepository, Tasklet testTasklet,PlatformTransactionManager transactionManager) {
        return new StepBuilder("validatorStep3", jobRepository)
                .tasklet(testTasklet, transactionManager)
                .build();
    }

    @Bean
    public Tasklet validatorTestTasklet() {
        return ((contribution, chunkContext) -> {
            String stepName = chunkContext.getStepContext().getStepName();
            System.out.println(stepName + " is execute");
            return RepeatStatus.FINISHED;
        });
    }
}

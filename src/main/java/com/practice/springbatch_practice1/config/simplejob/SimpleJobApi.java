package com.practice.springbatch_practice1.config.simplejob;

import org.springframework.batch.core.*;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
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
public class SimpleJobApi {

    @Bean
    public Job batchSimpleJob(JobRepository jobRepository, Step step1, Step step2) {
        return new JobBuilder("batchSimpleJob", jobRepository) // Job을 생성
                .start(step1) // 처음 실행 할 Step 설정, 최초 한번 설정, 이 메서드를 실행하면 SimpleJobBuilder 반환
                .next(step2) //  다음에 실행 할 Step 설정, 횟수는 제한이 없으며 모든 next() 의 Step 이 종료가 되면 Job 이 종료된다
                .incrementer(new RunIdIncrementer()) // JobParameter 의 값을 자동을 증가해 주는 JobParametersIncrementer 설정
                .validator(new JobParametersValidator() { // JobParameter 를 실행하기 전에 올바른 구성이 되었는지 검증하는 JobParametersValidator 설정
                    @Override
                    public void validate(JobParameters parameters) throws JobParametersInvalidException {

                    }
                })
                .preventRestart() // job 재시작 가능 여부를 설정한다. 기본은 true이며 설정하면 false로 된다.
                .listener(new JobExecutionListener() { // Job 라이프 사이클의 특정 시점에 콜백 제공받도록 JobExecutionListener 설정
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        JobExecutionListener.super.beforeJob(jobExecution);
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        JobExecutionListener.super.afterJob(jobExecution);
                    }
                })
                .build(); // SimpleJob 생성
    }

    @Bean
    public Step step1(JobRepository jobRepository, Tasklet testTasklet, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .tasklet(testTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step step2(JobRepository jobRepository, Tasklet testTasklet,PlatformTransactionManager transactionManager) {
        return new StepBuilder("step2", jobRepository)
                .tasklet(testTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step step3(JobRepository jobRepository, Tasklet testTasklet,PlatformTransactionManager transactionManager) {
        return new StepBuilder("step3", jobRepository)
                .tasklet(testTasklet, transactionManager)
                .build();
    }

    @Bean
    public Tasklet testTasklet() {
        return ((contribution, chunkContext) -> {
            String stepName = chunkContext.getStepContext().getStepName();
            System.out.println(stepName + " is execute");
            return RepeatStatus.FINISHED;
        });
    }
}

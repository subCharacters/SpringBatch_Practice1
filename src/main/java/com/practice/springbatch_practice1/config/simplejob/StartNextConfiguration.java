package com.practice.springbatch_practice1.config.simplejob;

import org.springframework.batch.core.*;
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
public class StartNextConfiguration {

    @Bean
    public Job startNextJob(JobRepository jobRepository, Step startNextstep1, Step step2, Step sampleStep) {
        return new JobBuilder("startNextJob", jobRepository) // Job을 생성 batch4에서의 get이 사라졌다. 정확히는 factory가 사라짐
                .start(startNextstep1) // 처음 실행 할 Step 설정, 최초 한번 설정, 이 메서드를 실행하면 SimpleJobBuilder 반환
                .next(step2) //  다음에 실행 할 Step 설정, 횟수는 제한이 없으며 모든 next() 의 Step 이 종료가 되면 Job 이 종료된다
                .next(sampleStep)
                .build(); // SimpleJob 생성
    }

    @Bean
    public Step startNextstep1(JobRepository jobRepository, Tasklet testTasklet, PlatformTransactionManager transactionManager) {
        return new StepBuilder("startNextstep1", jobRepository)
                .tasklet(testTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step startNextstep(JobRepository jobRepository, Tasklet testTasklet,PlatformTransactionManager transactionManager) {
        return new StepBuilder("startNextstep2", jobRepository)
                .tasklet(testTasklet, transactionManager)
                .build();
    }

    @Bean
    public Tasklet startNextTestTasklet() {
        return ((contribution, chunkContext) -> {
            String stepName = chunkContext.getStepContext().getStepName();
            System.out.println(stepName + " is execute");
            return RepeatStatus.FINISHED;
        });
    }
}

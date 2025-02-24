package com.practice.springbatch_practice1.config.flowjob;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class TransitionJob2 {

    @Bean
    public Job batchTransitionJob2(JobRepository jobRepository
            , Step transitionJob2Step1
            , Step transitionJob2Step2
            , Step transitionJob2Step3
            , Step transitionJob2Step4
            , Step transitionJob2Step5) {
        return new JobBuilder("batchTransitionJob2", jobRepository)
                .start(transitionJob2Step1)
                    .on("FAILED") //step1의 exit status가 failed일떄
                    .to(transitionJob2Step2) // step2를 실행
                    .on("FAILED") // step2가 실패할 경우
                    .stop() // job을 stop
                .from(transitionJob2Step1) // step1의 새로운 플로우 정의
                    .on("COMPLETED") // 위에 설정한 failed 이외이며 completed 일 경우
                    .to(transitionJob2Step3) // step3을 실행
                    .next(transitionJob2Step4) // step4를 실행
                .from(transitionJob2Step1) // step1의 새로운 플로우 정의
                    .on("*") // 위에 설정한 failed 와 complete 이외의 경우
                    .to(transitionJob2Step4) // step4를 실행
                .from(transitionJob2Step2) // step2의 새로운 플로우 정의
                    .on("*") // faile 이외의 경우
                    .to(transitionJob2Step5) // step5를 실행
                .end()
                .build();
    }

    @Bean
    public Step transitionJob2Step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("transitionJob2Step1", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 1 executed");
                    contribution.setExitStatus(ExitStatus.FAILED); // exit status를 정의
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }

    @Bean
    public Step transitionJob2Step2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("transitionJob2Step2", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 2 executed");
                    contribution.setExitStatus(ExitStatus.COMPLETED);
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }

    @Bean
    public Step transitionJob2Step3(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("transitionJob2Step3", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 3 executed");
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }

    @Bean
    public Step transitionJob2Step4(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("transitionJob2Step4", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 4 executed");
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }

    @Bean
    public Step transitionJob2Step5(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("transitionJob2Step5", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    System.out.println("Step 5 executed");
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }
}

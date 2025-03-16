package com.practice.springbatch_practice1.config.listener.jobstepexecution;

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
public class JobStepExecutionListenerConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JobStepExecutionListenerForStep listenerForStep;

    public JobStepExecutionListenerConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager, JobStepExecutionListenerForStep listenerForStep) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.listenerForStep = listenerForStep;
    }

    // 리스너에서 로그 출력은 물론 필요한 정보를 execution context를 통해서 다음 스탭에 연계하거나 exitstatus를 변경하는 것도 가능하다.
    @Bean
    public Job jobStepExecutionListenerJob() {
        return new JobBuilder("jobStepExecutionListenerJob", jobRepository)
                .start(jobStepExecutionListenerStep())
                .incrementer(new RunIdIncrementer())
                .listener(new JobStepExecutionListener())
                // 어노테이션 버전
                // .listener(new JobStepExecutionAnnotationListener())
                .build();
    }

    @Bean
    public Step jobStepExecutionListenerStep() {
        return new StepBuilder("jobStepExecutionListenerStep", jobRepository)
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        return RepeatStatus.FINISHED;
                    }
                }, transactionManager)
                // step도 job과 동일하게 어노테이션으로 정의도 가능.
                // 사용 시 빈으로 등록하여 DI 할 시 싱글톤으로 이용되기 때문에 동적으로 사용할 경우에는 주의가 필요하다.
                .listener(listenerForStep)
                .build();
    }
}

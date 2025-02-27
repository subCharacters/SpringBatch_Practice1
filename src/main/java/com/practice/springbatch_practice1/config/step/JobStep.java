package com.practice.springbatch_practice1.config.step;

import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.job.DefaultJobParametersExtractor;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class JobStep {

    @Bean
    public Job batchJopStep(JobRepository jobRepository, Step jobStep1, Step parentStep) {
        return new JobBuilder("batchJopStep", jobRepository)
                // 하위 job이 성공하고 재시작 하더라도 JobInstance already exists 예외는 발생하지 않는다.
                // 실행을 건너뛰고 바로 다음 step으로 간다.
                // allowStartIfComplete()설정을 하위 잡에 주면 재실행을 한다.
                // incrementer는 상위잡과 하위잡 모두 공유한다.
                // incrementer를 안한 상태에서 중간에 allowStartIfComplete를 추가하여 재실행하면 JobInstance already exists 예외가 발생한다.
                .start(jobStep1)
                .next(parentStep)
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step jobStep1(JobRepository jobRepository, Job childJob, JobLauncher jobLauncher) {
        return new StepBuilder("jobStep1", jobRepository)
                .job(childJob) // JobStep 내 에서 실행 될 Job 설정, JobStepBuilder 반환
                .launcher(jobLauncher) // Job 을 실행할 JobLauncher설정
                .parametersExtractor(parametersExtractor()) // Step의 ExecutionContext를 Job이 실행되는 데 필요한 JobParameters로 변환
                .allowStartIfComplete(true)
                .listener(new StepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        StepExecutionListener.super.beforeStep(stepExecution);
                        stepExecution.getExecutionContext().put("user", "users1");
                    }

                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        return StepExecutionListener.super.afterStep(stepExecution);
                    }
                })
                .build();
    }

    // DefaultJobParametersExtractor는 스프링 배치에서 제공하는 기본 클래스
    // execute context안에 정의된 키 정보를 가지고 온다.
    private DefaultJobParametersExtractor parametersExtractor() {
        DefaultJobParametersExtractor defaultJobParametersExtractor = new DefaultJobParametersExtractor();
        defaultJobParametersExtractor.setKeys(new String[]{"user"});
        return defaultJobParametersExtractor;
    }

    @Bean
    public Job childJob(JobRepository jobRepository, Step childStep) {
        return new JobBuilder("childJob", jobRepository)
                .start(childStep)
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step childStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("childStep", jobRepository)
                .tasklet(tasklet1(), transactionManager)
                .build();
    }

    @Bean
    public Step parentStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("parentStep", jobRepository)
                .tasklet(tasklet2(), transactionManager)
                .build();
    }


    public Tasklet tasklet1() {
        return ((contribution, chunkContext) -> {
            String stepName = chunkContext.getStepContext().getStepExecution().getStepName();
            System.out.println(stepName + " tasklet");
            return RepeatStatus.FINISHED;
        });
    }

    public Tasklet tasklet2() {
        return ((contribution, chunkContext) -> {
            String stepName = chunkContext.getStepContext().getStepExecution().getStepName();
            throw new RuntimeException("failed");
            // return RepeatStatus.FINISHED;
        });
    }
}

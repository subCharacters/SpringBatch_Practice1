package com.practice.springbatch_practice1.config.repeatanderrorhandling.repeat;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class FaultTolerantConfiguration {
    // StepBuilder -> FaultTolerantStepBuilder -> Tasklet

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public FaultTolerantConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job faultTolerantJob() {
        return new JobBuilder("faultTolerantJob", jobRepository)
                .start(faultTolerantStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    /**
     * skip은 reader process writer에 모두 있지만
     * reader에는 retry기능이 없다.
     */
    @Bean
    public Step faultTolerantStep() {
        return new StepBuilder("faultTolerantStep", jobRepository)
                .<String, String>chunk(3, transactionManager)
                .reader(new ItemReader<String>() {
                    int i = 0;
                    @Override
                    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                        i++;

                        if (i == 1) {
                            throw new IllegalArgumentException("this exception was skipped");
                        }

                        return i <= 3 ? "item" + i : null;
                    }
                })
                .processor(new ItemProcessor<String, String>() {
                    @Override
                    public String process(String item) throws Exception {
                        System.out.println(item); // retry 2번까지. 2회 출력됨.
                        throw new IllegalStateException("this exception was retried");
                    }
                })
                .writer(chunk -> chunk.getItems().forEach(System.out::println))
                .faultTolerant()
                .skip(IllegalArgumentException.class)
                .skipLimit(2)
                .retry(IllegalStateException.class)
                .retryLimit(2)
                .build();
    }
}
